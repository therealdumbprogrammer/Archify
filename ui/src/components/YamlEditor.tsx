type YamlEditorProps = {
  value: string;
  error: string;
  onChange: (next: string) => void;
};

export default function YamlEditor({ value, error, onChange }: YamlEditorProps) {
  return (
    <section className="space-y-2 rounded-xl border border-slate-700 bg-panel/90 p-4">
      <h2 className="text-lg font-semibold">YAML</h2>
      <textarea
        className="h-80 w-full rounded border border-slate-600 bg-slate-950 p-3 font-mono text-sm"
        value={value}
        onChange={(event) => onChange(event.target.value)}
      />
      {error && <p className="text-sm text-red-300">{error}</p>}
    </section>
  );
}
