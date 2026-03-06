type HeroSectionProps = {
  onHomeClick: () => void;
};

export default function HeroSection({ onHomeClick }: HeroSectionProps) {
  return (
    <section className="rounded-2xl border border-slate-700/80 bg-gradient-to-r from-slate-900 via-slate-900 to-slate-800 p-6">
      <button
        type="button"
        className="text-left text-4xl font-bold tracking-tight text-white hover:text-slate-200"
        onClick={onHomeClick}
      >
        Archify
      </button>
      <h1 className="mt-2 text-xl font-semibold text-slate-200">Architecture {'->'} Spring Boot Code</h1>
      <p className="mt-3 max-w-2xl text-slate-300">
        Generate fully wired Spring Boot services from architecture recipes.
      </p>
    </section>
  );
}
