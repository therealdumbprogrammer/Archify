type GenerateButtonProps = {
  disabled: boolean;
  onClick: () => void;
};

export default function GenerateButton({ disabled, onClick }: GenerateButtonProps) {
  return (
    <button
      type="button"
      className="w-full rounded-lg bg-accent px-5 py-4 text-base font-semibold text-white transition hover:brightness-110 disabled:cursor-not-allowed disabled:opacity-40"
      disabled={disabled}
      onClick={onClick}
    >
      Generate Spring Boot Project
    </button>
  );
}
