import { EntityField } from '../types';

const FIELD_TYPES = ['String', 'Long', 'Integer', 'Boolean', 'Double', 'LocalDate', 'LocalDateTime', 'BigDecimal'];

type FieldEditorProps = {
  field: EntityField;
  onChange: (field: EntityField) => void;
  onRemove: () => void;
  disableRemove?: boolean;
};

export default function FieldEditor({ field, onChange, onRemove, disableRemove }: FieldEditorProps) {
  return (
    <div className="grid grid-cols-12 items-center gap-2 rounded border border-slate-700 bg-slate-900/70 p-2">
      <input
        className="col-span-5 rounded border border-slate-600 bg-slate-950 px-2 py-1"
        value={field.name}
        onChange={(event) => onChange({ ...field, name: event.target.value })}
        placeholder="field name"
        disabled={field.name === 'id'}
      />
      <select
        className="col-span-5 rounded border border-slate-600 bg-slate-950 px-2 py-1"
        value={field.type}
        onChange={(event) => onChange({ ...field, type: event.target.value })}
        disabled={field.name === 'id'}
      >
        {FIELD_TYPES.map((type) => (
          <option key={type} value={type}>
            {type}
          </option>
        ))}
      </select>
      <button
        type="button"
        className="col-span-2 rounded bg-slate-700 px-2 py-1 text-sm disabled:opacity-40"
        onClick={onRemove}
        disabled={disableRemove || field.name === 'id'}
      >
        Remove
      </button>
    </div>
  );
}
