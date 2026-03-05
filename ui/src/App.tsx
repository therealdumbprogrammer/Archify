import { useEffect, useMemo, useState } from 'react';
import { parse, stringify } from 'yaml';
import ConfigPanel from './components/ConfigPanel';
import DownloadPanel from './components/DownloadPanel';
import GenerateButton from './components/GenerateButton';
import RecipeSelector from './components/RecipeSelector';
import YamlEditor from './components/YamlEditor';
import { RecipeDefinition } from './types';

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080';

type Mode = 'form' | 'yaml';

export default function App() {
  const [recipes, setRecipes] = useState<RecipeDefinition[]>([]);
  const [selectedRecipe, setSelectedRecipe] = useState('');
  const [config, setConfig] = useState<Record<string, unknown>>({});
  const [mode, setMode] = useState<Mode>('form');
  const [yamlText, setYamlText] = useState('');
  const [yamlError, setYamlError] = useState('');
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

  useEffect(() => {
    if (!currentRecipe) {
      return;
    }
    const nextConfig = defaultConfigForRecipe(currentRecipe);
    setConfig(nextConfig);
    setYamlText(stringify(nextConfig));
    setYamlError('');
  }, [currentRecipe]);

  const handleConfigChange = (nextConfig: Record<string, unknown>) => {
    setConfig(nextConfig);
    setYamlText(stringify(nextConfig));
    setYamlError('');
  };

  const handleYamlChange = (nextYaml: string) => {
    setYamlText(nextYaml);
    try {
      const parsed = parse(nextYaml);
      if (!parsed || typeof parsed !== 'object' || Array.isArray(parsed)) {
        throw new Error('YAML must define an object at the root');
      }
      setConfig(parsed as Record<string, unknown>);
      setYamlError('');
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Invalid YAML';
      setYamlError(message);
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
      const url = URL.createObjectURL(blob);
      link.href = url;
      link.download = 'generated-project.zip';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
      setStatus('ZIP generated and download started.');
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to generate project';
      setStatus(`Generation failed: ${message}`);
    } finally {
      setGenerating(false);
    }
  };

  return (
    <main className="mx-auto min-h-screen max-w-6xl p-4 md:p-8">
      <header className="mb-6">
        <h1 className="text-3xl font-bold">Archify V1</h1>
        <p className="mt-1 text-sm text-slate-300">Generate fully wired Spring Boot services from architecture recipes.</p>
      </header>

      <div className="grid gap-4 lg:grid-cols-2">
        <div className="space-y-4">
          <RecipeSelector recipes={recipes} selectedRecipe={selectedRecipe} onSelect={setSelectedRecipe} />

          <section className="rounded-xl border border-slate-700 bg-panel/90 p-4">
            <div className="mb-3 flex gap-2">
              <button
                type="button"
                className={`rounded px-3 py-1 text-sm ${mode === 'form' ? 'bg-accent text-white' : 'bg-slate-700'}`}
                onClick={() => setMode('form')}
              >
                Form mode
              </button>
              <button
                type="button"
                className={`rounded px-3 py-1 text-sm ${mode === 'yaml' ? 'bg-accent text-white' : 'bg-slate-700'}`}
                onClick={() => setMode('yaml')}
              >
                YAML mode
              </button>
            </div>

            {mode === 'form' ? (
              <ConfigPanel recipe={currentRecipe} config={config} onConfigChange={handleConfigChange} />
            ) : (
              <YamlEditor value={yamlText} error={yamlError} onChange={handleYamlChange} />
            )}
          </section>
        </div>

        <div className="space-y-4">
          <section className="rounded-xl border border-slate-700 bg-panel/90 p-4">
            <h2 className="mb-3 text-lg font-semibold">Generate</h2>
            <GenerateButton
              disabled={generating || loadingRecipes || !selectedRecipe || (mode === 'yaml' && !!yamlError)}
              onClick={handleGenerate}
            />
          </section>

          <DownloadPanel status={status} />
        </div>
      </div>
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
        config[input.name] = '';
    }
  }

  return config;
}
