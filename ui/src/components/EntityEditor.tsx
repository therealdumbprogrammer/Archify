import { EntityConfig } from '../types';
import FieldEditor from './FieldEditor';

const defaultEntity = (): EntityConfig => ({
  name: 'Entity',
  fields: [{ name: 'id', type: 'Long' }]
});

type EntityEditorProps = {
  entities: EntityConfig[];
  onChange: (entities: EntityConfig[]) => void;
};

export default function EntityEditor({ entities, onChange }: EntityEditorProps) {
  const updateEntity = (index: number, next: EntityConfig) => {
    const updated = [...entities];
    updated[index] = normalizeEntity(next);
    onChange(updated);
  };

  const removeEntity = (index: number) => {
    const updated = entities.filter((_, currentIndex) => currentIndex !== index);
    onChange(updated);
  };

  const addEntity = () => {
    onChange([...entities, defaultEntity()]);
  };

  const addField = (entityIndex: number) => {
    const entity = entities[entityIndex];
    updateEntity(entityIndex, {
      ...entity,
      fields: [...entity.fields, { name: 'field', type: 'String' }]
    });
  };

  const updateField = (entityIndex: number, fieldIndex: number, field: { name: string; type: string }) => {
    const entity = entities[entityIndex];
    const fields = [...entity.fields];
    fields[fieldIndex] = field;
    updateEntity(entityIndex, { ...entity, fields });
  };

  const removeField = (entityIndex: number, fieldIndex: number) => {
    const entity = entities[entityIndex];
    const fields = entity.fields.filter((_, currentIndex) => currentIndex !== fieldIndex);
    updateEntity(entityIndex, { ...entity, fields });
  };

  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-base font-semibold">Entities</h3>
        <button type="button" className="rounded bg-accent px-3 py-1 text-sm text-white" onClick={addEntity}>
          Add Entity
        </button>
      </div>
      <p className="text-xs text-slate-400">
        Entity = domain object/table (example: User). Field = property/column (example: email as String).
      </p>
      {entities.map((entity, entityIndex) => (
        <div key={entityIndex} className="rounded-lg border border-slate-700 bg-slate-900/60 p-3">
          <div className="mb-2 flex items-center gap-2">
            <input
              className="flex-1 rounded border border-slate-600 bg-slate-950 px-2 py-1"
              value={entity.name}
              onChange={(event) => updateEntity(entityIndex, { ...entity, name: event.target.value })}
              placeholder="Entity name"
            />
            <button
              type="button"
              className="rounded bg-slate-700 px-2 py-1 text-sm"
              onClick={() => removeEntity(entityIndex)}
            >
              Remove Entity
            </button>
          </div>
          <div className="space-y-2">
            {entity.fields.map((field, fieldIndex) => (
              <FieldEditor
                key={fieldIndex}
                field={field}
                onChange={(next) => updateField(entityIndex, fieldIndex, next)}
                onRemove={() => removeField(entityIndex, fieldIndex)}
                disableRemove={entity.fields.length <= 1}
              />
            ))}
            <button
              type="button"
              className="rounded bg-slate-700 px-2 py-1 text-sm"
              onClick={() => addField(entityIndex)}
            >
              Add Field
            </button>
          </div>
        </div>
      ))}
    </section>
  );
}

function normalizeEntity(entity: EntityConfig): EntityConfig {
  const withoutId = entity.fields.filter((field) => field.name.toLowerCase() !== 'id');
  return {
    ...entity,
    fields: [{ name: 'id', type: 'Long' }, ...withoutId]
  };
}
