type DownloadPanelProps = {
  status: string;
};

export default function DownloadPanel({ status }: DownloadPanelProps) {
  return (
    <section className="rounded-xl border border-slate-700 bg-panel/90 p-4">
      <h2 className="text-lg font-semibold">Download</h2>
      <p className="mt-2 text-sm text-slate-300">{status || 'Generated ZIP download status will appear here.'}</p>
    </section>
  );
}
