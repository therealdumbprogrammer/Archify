export default function GenerationMetadata() {
  const items = [
    { label: 'Spring Boot', value: '4.0.2' },
    { label: 'JDK', value: '25 (LTS)' },
    { label: 'Maven Wrapper', value: '3.9.9' }
  ];

  return (
    <section className="rounded-xl border border-slate-700/80 bg-panel/90 p-4">
      <h2 className="text-lg font-semibold">Generation Metadata</h2>
      <p className="mt-1 text-sm text-slate-300">Generated projects target the following baseline toolchain.</p>
      <div className="mt-4 grid gap-3 sm:grid-cols-3">
        {items.map((item) => (
          <div key={item.label} className="rounded-lg border border-slate-700 bg-slate-950/40 p-3">
            <div className="text-xs uppercase tracking-wide text-slate-400">{item.label}</div>
            <div className="mt-1 text-lg font-semibold text-white">{item.value}</div>
          </div>
        ))}
      </div>
    </section>
  );
}
