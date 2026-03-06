type StepIndicatorProps = {
  currentStep: number;
  maxAvailableStep: number;
  onStepSelect: (step: number) => void;
};

const STEPS = [
  { id: 1, label: 'Select Recipe' },
  { id: 2, label: 'Configure Architecture' },
  { id: 3, label: 'Generate Project' }
];

export default function StepIndicator({ currentStep, maxAvailableStep, onStepSelect }: StepIndicatorProps) {
  return (
    <section className="rounded-xl border border-slate-700/80 bg-panel/90 p-4">
      <div className="flex flex-wrap items-center gap-2">
        {STEPS.map((step, index) => {
          const active = step.id === currentStep;
          const accessible = step.id <= maxAvailableStep;

          return (
            <div key={step.id} className="flex items-center gap-2">
              <button
                type="button"
                className={`rounded-md border px-3 py-2 text-sm transition ${
                  active
                    ? 'border-accent bg-accent/20 text-white'
                    : accessible
                      ? 'border-slate-600 bg-slate-900/70 text-slate-200 hover:bg-slate-900'
                      : 'cursor-not-allowed border-slate-800 bg-slate-900/40 text-slate-500'
                }`}
                disabled={!accessible}
                onClick={() => onStepSelect(step.id)}
              >
                {step.id} {step.label}
              </button>
              {index < STEPS.length - 1 && <span className="text-slate-500">→</span>}
            </div>
          );
        })}
      </div>
    </section>
  );
}
