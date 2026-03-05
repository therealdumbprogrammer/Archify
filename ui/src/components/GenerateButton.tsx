type GenerateButtonProps = {
  disabled: boolean;
  onClick: () => void;
};

export default function GenerateButton({ disabled, onClick }: GenerateButtonProps) {
  return (
    <button
      type="button"
      className="rounded-md bg-accent px-4 py-2 font-semibold text-white transition hover:brightness-110 disabled:cursor-not-allowed disabled:opacity-40"
      disabled={disabled}
      onClick={onClick}
    >
      Generate Project ZIP
    </button>
  );
}
