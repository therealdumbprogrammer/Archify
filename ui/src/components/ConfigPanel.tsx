import EntityEditor from './EntityEditor';
import { EntityConfig, RecipeDefinition } from '../types';

type ConfigPanelProps = {
  recipe: RecipeDefinition | null;
  config: Record<string, unknown>;
  onConfigChange: (config: Record<string, unknown>) => void;
};

export default function ConfigPanel({ recipe, config, onConfigChange }: ConfigPanelProps) {
  if (!recipe) {
    return (
      <section className="rounded-xl border border-slate-700 bg-panel/90 p-4">
        <p className="text-sm text-slate-300">Select a recipe to configure inputs.</p>
      </section>
    );
  }

  const setInputValue = (key: string, value: unknown) => {
    onConfigChange({ ...config, [key]: value });
  };

  return (
    <section className="space-y-4 rounded-xl border border-slate-700 bg-panel/90 p-4">
      <h2 className="text-lg font-semibold">Project Configuration</h2>
      {recipe.inputs.map((input) => {
        if (input.type === 'entityList') {
          const entities = normalizeEntities(config[input.name]);
          return (
            <EntityEditor
              key={input.name}
              entities={entities}
              onChange={(value) => setInputValue(input.name, value)}
            />
          );
        }

        if (input.type === 'communication') {
          return (
            <div key={input.name} className="space-y-1">
              <label className="text-sm text-slate-300">{input.name}</label>
              <select
                className="w-full rounded border border-slate-600 bg-slate-950 px-3 py-2"
                value={String(config[input.name] ?? 'FEIGN')}
                onChange={(event) => setInputValue(input.name, event.target.value)}
              >
                <option value="FEIGN">FEIGN</option>
              </select>
            </div>
          );
        }

        if (input.type === 'databaseType') {
          return (
            <div key={input.name} className="space-y-1">
              <label className="text-sm text-slate-300">{input.name}</label>
              <select
                className="w-full rounded border border-slate-600 bg-slate-950 px-3 py-2"
                value={String(config[input.name] ?? 'NONE')}
                onChange={(event) => setInputValue(input.name, event.target.value)}
              >
                <option value="NONE">NONE</option>
                <option value="POSTGRES">POSTGRES</option>
              </select>
            </div>
          );
        }

        return (
          <div key={input.name} className="space-y-1">
            <label className="text-sm text-slate-300">{input.name}</label>
            {inputHelpText(input.name) && <p className="text-xs text-slate-400">{inputHelpText(input.name)}</p>}
            <input
              className="w-full rounded border border-slate-600 bg-slate-950 px-3 py-2"
              value={String(config[input.name] ?? '')}
              onChange={(event) => setInputValue(input.name, event.target.value)}
              placeholder={input.name}
            />
          </div>
        );
      })}
    </section>
  );
}

function normalizeEntities(value: unknown): EntityConfig[] {
  if (!Array.isArray(value)) {
    return [{ name: 'Entity', fields: [{ name: 'id', type: 'Long' }] }];
  }

  return value.map((entity) => {
    if (!entity || typeof entity !== 'object') {
      return { name: 'Entity', fields: [{ name: 'id', type: 'Long' }] };
    }

    const typedEntity = entity as EntityConfig;
    const fields = Array.isArray(typedEntity.fields) ? typedEntity.fields : [];
    const withoutId = fields.filter((field) => field.name.toLowerCase() !== 'id');
    return {
      name: typedEntity.name || 'Entity',
      fields: [{ name: 'id', type: 'Long' }, ...withoutId]
    };
  });
}

function inputHelpText(inputName: string): string {
  if (inputName === 'serviceName' || inputName === 'serviceAName' || inputName === 'serviceBName') {
    return 'Service name becomes the generated app/module name (example: user-service).';
  }
  return '';
}
