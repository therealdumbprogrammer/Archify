import { useEffect, useMemo, useState } from 'react';
import { parse, stringify } from 'yaml';
import ArchitecturePreview from './components/ArchitecturePreview';
import ConfigPanel from './components/ConfigPanel';
import GenerationMetadata from './components/GenerationMetadata';
import GenerateButton from './components/GenerateButton';
import HeroSection from './components/HeroSection';
import RecipeSelector from './components/RecipeSelector';
import StepIndicator from './components/StepIndicator';
import YamlEditor from './components/YamlEditor';
import { ArchitectureSpec, EntityConfig, RecipeDefinition } from './types';

const API_BASE = (import.meta.env.VITE_API_URL || 'http://localhost:8080').replace(/\/$/, '');

type Mode = 'form' | 'yaml';
type InputMethod = 'recipe' | 'yaml';

export default function App() {
  const [step, setStep] = useState(1);
  const [inputMethod, setInputMethod] = useState<InputMethod>('recipe');
  const [recipes, setRecipes] = useState<RecipeDefinition[]>([]);
  const [selectedRecipe, setSelectedRecipe] = useState('');
  const [config, setConfig] = useState<Record<string, unknown>>({});
  const [mode, setMode] = useState<Mode>('form');
  const [yamlText, setYamlText] = useState('');
  const [yamlError, setYamlError] = useState('');
  const [step1YamlInput, setStep1YamlInput] = useState('');
  const [step1YamlError, setStep1YamlError] = useState('');
  const [status, setStatus] = useState('');
  const [loadingRecipes, setLoadingRecipes] = useState(false);
  const [generating, setGenerating] = useState(false);

  const currentRecipe = useMemo(
    () => recipes.find((recipe) => recipe.name === selectedRecipe) ?? null,
    [recipes, selectedRecipe]
  );

  useEffect(() => {
    setLoadingRecipes(true);
    fetch(`${API_BASE}/recipes`)
      .then(async (response) => {
        if (!response.ok) {
          throw new Error(await response.text());
        }
        return response.json() as Promise<RecipeDefinition[]>;
      })
      .then((response) => setRecipes(response))
      .catch((error: Error) => setStatus(`Failed to load recipes: ${error.message}`))
      .finally(() => setLoadingRecipes(false));
  }, []);

  const handleConfigChange = (nextConfig: Record<string, unknown>) => {
    setConfig(nextConfig);
    setYamlText(toYamlSpec(selectedRecipe, nextConfig));
    setYamlError('');
  };

  const handleYamlChange = (nextYaml: string) => {
    setYamlText(nextYaml);
    try {
      const { recipe, parsedConfig } = parseArchitectureSpec(nextYaml, recipes);
      setSelectedRecipe(recipe);
      setConfig(parsedConfig);
      setYamlError('');
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Invalid YAML';
      setYamlError(message);
    }
  };

  const handleCopyYaml = async () => {
    try {
      await navigator.clipboard.writeText(yamlText);
      setStatus('YAML copied to clipboard.');
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Clipboard write failed';
      setStatus(`Failed to copy YAML: ${message}`);
    }
  };

  const handlePasteStep1Yaml = async () => {
    try {
      const text = await navigator.clipboard.readText();
      if (!text.trim()) {
        setStatus('Clipboard is empty.');
        return;
      }
      setStep1YamlInput(text);
      try {
        parseArchitectureSpec(text, recipes);
        setStep1YamlError('');
      } catch (error) {
        const message = error instanceof Error ? error.message : 'Invalid YAML';
        setStep1YamlError(message);
      }
      setStatus('YAML pasted into Step 1.');
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Clipboard read failed';
      setStatus(`Failed to paste YAML: ${message}`);
    }
  };

  const handleStep1YamlInputChange = (nextYaml: string) => {
    setStep1YamlInput(nextYaml);
    try {
      parseArchitectureSpec(nextYaml, recipes);
      setStep1YamlError('');
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Invalid YAML';
      setStep1YamlError(message);
    }
  };

  const handleApplyStep1Yaml = () => {
    try {
      const { recipe, parsedConfig } = parseArchitectureSpec(step1YamlInput, recipes);
      setSelectedRecipe(recipe);
      setConfig(parsedConfig);
      setYamlText(toYamlSpec(recipe, parsedConfig));
      setYamlError('');
      setStep1YamlError('');
      setMode('yaml');
      setStep(2);
      setStatus('');
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Invalid YAML';
      setStep1YamlError(message);
    }
  };

  const handleGenerate = async () => {
    if (!selectedRecipe) {
      setStatus('Select a recipe first.');
      return;
    }

    setGenerating(true);
    setStatus('Generating project ZIP...');

    try {
      const response = await fetch(`${API_BASE}/generate`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/zip'
        },
        body: JSON.stringify({
          recipe: selectedRecipe,
          config
        })
      });

      if (!response.ok) {
        throw new Error(await response.text());
      }

      const blob = await response.blob();
      const link = document.createElement('a');
      const url = window.URL.createObjectURL(blob);
      link.href = url;
      link.download = 'archify-project.zip';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      setStatus('ZIP generated and download started.');
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to generate project';
      setStatus(`Generation failed: ${message}`);
    } finally {
      setGenerating(false);
    }
  };

  const handleRecipeSelect = (recipeName: string) => {
    const recipe = recipes.find((candidate) => candidate.name === recipeName);
    const nextConfig = recipe ? defaultConfigForRecipe(recipe) : {};
    setSelectedRecipe(recipeName);
    setConfig(nextConfig);
    setYamlText(toYamlSpec(recipeName, nextConfig));
    setYamlError('');
    setStep(2);
    setMode('form');
    setStatus('');
  };

  const step1Valid = Boolean(selectedRecipe);
  const step2Valid = step1Valid && validateConfig(currentRecipe, config) && !yamlError;
  const maxAvailableStep = step1Valid ? 2 : 1;

  const switchStep = (nextStep: number) => {
    if (nextStep <= maxAvailableStep) {
      setStep(nextStep);
    }
  };

  const handleHomeClick = () => {
    setStep(1);
  };

  return (
    <main className="mx-auto min-h-screen max-w-7xl space-y-4 p-4 md:p-8">
      <HeroSection onHomeClick={handleHomeClick} />
      <StepIndicator currentStep={step} maxAvailableStep={maxAvailableStep} onStepSelect={switchStep} />

      <div className="grid gap-4 lg:grid-cols-[1.2fr_1fr]">
        <div className="space-y-4">
          {step === 1 && (
            <>
              <section className="space-y-3 rounded-xl border border-slate-700/80 bg-panel/90 p-4">
                <h2 className="text-lg font-semibold">Select Architecture Input</h2>
                <div className="flex flex-wrap gap-2">
                  <button
                    type="button"
                    className={`rounded px-3 py-1 text-sm ${inputMethod === 'recipe' ? 'bg-accent text-white' : 'bg-slate-700'}`}
                    onClick={() => setInputMethod('recipe')}
                  >
                    Recipe Cards
                  </button>
                  <button
                    type="button"
                    className={`rounded px-3 py-1 text-sm ${inputMethod === 'yaml' ? 'bg-accent text-white' : 'bg-slate-700'}`}
                    onClick={() => {
                      setInputMethod('yaml');
                      if (!step1YamlInput.trim()) {
                        setStep1YamlInput(yamlText || defaultStep1Yaml());
                      }
                    }}
                  >
                    YAML Spec
                  </button>
                </div>
              </section>

              {inputMethod === 'recipe' ? (
                <RecipeSelector
                  recipes={recipes}
                  selectedRecipe={selectedRecipe}
                  onSelect={handleRecipeSelect}
                  loadingRecipes={loadingRecipes}
                />
              ) : (
                <section className="space-y-3 rounded-xl border border-slate-700/80 bg-panel/90 p-4">
                  <div className="flex flex-wrap gap-2">
                    <button type="button" className="rounded bg-slate-700 px-3 py-1 text-sm" onClick={handlePasteStep1Yaml}>
                      Paste YAML
                    </button>
                    <button
                      type="button"
                      className="rounded bg-accent px-3 py-1 text-sm text-white disabled:opacity-50"
                      disabled={!step1YamlInput.trim() || !!step1YamlError}
                      onClick={handleApplyStep1Yaml}
                    >
                      Apply YAML And Continue
                    </button>
                  </div>
                  <YamlEditor value={step1YamlInput} error={step1YamlError} onChange={handleStep1YamlInputChange} />
                </section>
              )}
            </>
          )}

          {step >= 2 && (
            <section className="space-y-4 rounded-xl border border-slate-700/80 bg-panel/90 p-4">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <h2 className="text-lg font-semibold">Configure Project</h2>
                  <p className="mt-1 text-sm text-slate-300">
                    Update the service, entities, and fields, then generate the project ZIP from this same workspace.
                  </p>
                </div>
                <div className="flex flex-wrap items-center gap-2">
                  <button
                    type="button"
                    className={`rounded px-3 py-1 text-sm ${mode === 'form' ? 'bg-accent text-white' : 'bg-slate-700'}`}
                    onClick={() => setMode('form')}
                  >
                    Project Form
                  </button>
                  <button
                    type="button"
                    className={`rounded px-3 py-1 text-sm ${mode === 'yaml' ? 'bg-accent text-white' : 'bg-slate-700'}`}
                    onClick={() => setMode('yaml')}
                  >
                    YAML Editor
                  </button>
                  {mode === 'yaml' && (
                    <button type="button" className="rounded border border-slate-600 bg-slate-900 px-3 py-1 text-sm" onClick={handleCopyYaml}>
                      Copy YAML
                    </button>
                  )}
                </div>
              </div>

              {mode === 'form' ? (
                <ConfigPanel recipe={currentRecipe} config={config} onConfigChange={handleConfigChange} />
              ) : (
                <YamlEditor value={yamlText} error={yamlError} onChange={handleYamlChange} />
              )}

              <section className="rounded-xl border border-slate-700/80 bg-slate-950/40 p-4">
                <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                  <div>
                    <h3 className="text-lg font-semibold">Generate Project</h3>
                    <p className="mt-1 text-sm text-slate-300">
                      {step2Valid
                        ? 'Project configuration looks valid. Generate the ZIP when ready.'
                        : 'Complete the required project fields before generating.'}
                    </p>
                  </div>
                  <GenerateButton disabled={generating || loadingRecipes || !step2Valid} onClick={handleGenerate} />
                </div>
                <p className="mt-3 text-sm text-slate-300">{status || 'Generated ZIP download status will appear here.'}</p>
              </section>
            </section>
          )}
        </div>

        <div className="space-y-4">
          <GenerationMetadata />
          <ArchitecturePreview recipe={currentRecipe} config={config} />
        </div>
      </div>

      <footer className="flex flex-wrap items-center gap-3 border-t border-slate-800 pt-4 text-sm text-slate-400">
        <span>© 2026 Archify</span>
        <a className="hover:text-slate-200" href="/LICENSE" target="_blank" rel="noreferrer">
          Apache-2.0
        </a>
        <a
          className="hover:text-slate-200"
          href="https://github.com/therealdumbprogrammer/Archify"
          target="_blank"
          rel="noreferrer"
        >
          GitHub
        </a>
      </footer>
    </main>
  );
}

function defaultConfigForRecipe(recipe: RecipeDefinition): Record<string, unknown> {
  const config: Record<string, unknown> = {};

  for (const input of recipe.inputs) {
    switch (input.type) {
      case 'entityList':
        config[input.name] = [{ name: 'Entity', fields: [{ name: 'id', type: 'Long' }] }];
        break;
      case 'communication':
        config[input.name] = 'FEIGN';
        break;
      case 'databaseType':
        config[input.name] = 'NONE';
        break;
      default:
        config[input.name] = defaultValueForInput(recipe, input.name);
    }
  }

  return config;
}

function defaultValueForInput(recipe: RecipeDefinition, inputName: string): string {
  if (inputName === 'serviceName') {
    return 'app-service';
  }
  if (inputName === 'serviceAName') {
    return 'user-service';
  }
  if (inputName === 'serviceBName') {
    return 'order-service';
  }
  if (inputName === 'database') {
    return recipe.name.includes('postgres') ? 'POSTGRES' : 'NONE';
  }
  return '';
}

function toYamlSpec(recipe: string, config: Record<string, unknown>): string {
  return stringify({
    recipe,
    ...config
  });
}

function toConfig(spec: ArchitectureSpec): Record<string, unknown> {
  const nextConfig: Record<string, unknown> = { ...spec };
  delete nextConfig.recipe;
  return nextConfig;
}

function validateConfig(recipe: RecipeDefinition | null, config: Record<string, unknown>): boolean {
  if (!recipe) {
    return false;
  }
  for (const input of recipe.inputs) {
    const value = config[input.name];
    if (!input.required) {
      continue;
    }
    if (input.type === 'entityList') {
      const entities = normalizeEntities(value);
      if (entities.length === 0) {
        return false;
      }
      const allEntitiesValid = entities.every((entity) => {
        const hasName = entity.name.trim().length > 0;
        const hasFields = entity.fields.length > 0;
        const fieldsValid = entity.fields.every((field) => field.name.trim().length > 0 && field.type.trim().length > 0);
        return hasName && hasFields && fieldsValid;
      });
      if (!allEntitiesValid) {
        return false;
      }
      continue;
    }
    if (typeof value !== 'string' || value.trim().length === 0) {
      return false;
    }
  }
  return true;
}

function normalizeEntities(value: unknown): EntityConfig[] {
  if (!Array.isArray(value)) {
    return [];
  }
  return value
    .filter((entity): entity is EntityConfig => Boolean(entity) && typeof entity === 'object')
    .map((entity) => ({
      name: typeof entity.name === 'string' ? entity.name : '',
      fields: Array.isArray(entity.fields)
        ? entity.fields
            .filter((field): field is { name: string; type: string } => Boolean(field) && typeof field === 'object')
            .map((field) => ({
              name: typeof field.name === 'string' ? field.name : '',
              type: typeof field.type === 'string' ? field.type : ''
            }))
        : []
    }));
}

function parseArchitectureSpec(yaml: string, recipes: RecipeDefinition[]): { recipe: string; parsedConfig: Record<string, unknown> } {
  const parsed = parse(yaml);
  if (!parsed || typeof parsed !== 'object' || Array.isArray(parsed) || typeof parsed.recipe !== 'string') {
    throw new Error('YAML must define an object root and include "recipe".');
  }

  const parsedSpec = parsed as ArchitectureSpec;
  const recipe = parsedSpec.recipe.trim();
  if (!recipe) {
    throw new Error('YAML field "recipe" is required.');
  }
  if (recipes.length > 0 && !recipes.some((candidate) => candidate.name === recipe)) {
    throw new Error(`Unknown recipe "${recipe}".`);
  }

  return {
    recipe,
    parsedConfig: toConfig(parsedSpec)
  };
}

function defaultStep1Yaml(): string {
  return stringify({
    recipe: 'rest-postgres',
    serviceName: 'user-service',
    entities: [
      {
        name: 'User',
        fields: [
          { name: 'id', type: 'Long' },
          { name: 'name', type: 'String' }
        ]
      }
    ]
  });
}
