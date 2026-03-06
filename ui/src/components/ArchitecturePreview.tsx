import { useMemo } from 'react';
import ReactFlow, { Background, Controls, Edge, MarkerType, Node } from 'reactflow';
import 'reactflow/dist/style.css';
import { ArchitectureSpec, RecipeDefinition } from '../types';

type ArchitecturePreviewProps = {
  recipe: RecipeDefinition | null;
  config: Record<string, unknown>;
};

export default function ArchitecturePreview({ recipe, config }: ArchitecturePreviewProps) {
  const spec = useMemo(() => ({ ...(config as ArchitectureSpec), recipe: recipe?.name ?? '' }), [config, recipe]);
  const graph = useMemo(() => buildDiagram(recipe, spec), [recipe, spec]);

  return (
    <section className="h-[560px] rounded-xl border border-slate-700/80 bg-panel/90 p-3">
      <h2 className="mb-3 text-lg font-semibold">Architecture Preview</h2>
      <div className="h-[500px] rounded-lg border border-slate-700 bg-slate-950/50">
        <ReactFlow fitView nodes={graph.nodes} edges={graph.edges} proOptions={{ hideAttribution: true }}>
          <Background color="#334155" gap={20} />
          <Controls />
        </ReactFlow>
      </div>
    </section>
  );
}

function buildDiagram(
  recipe: RecipeDefinition | null,
  spec: ArchitectureSpec
): {
  nodes: Node[];
  edges: Edge[];
} {
  if (recipe?.diagram?.nodes.length) {
    return buildFromRecipeDiagram(recipe, spec);
  }
  return buildFallbackDiagram(recipe, spec);
}

function buildFromRecipeDiagram(
  recipe: RecipeDefinition,
  spec: ArchitectureSpec
): {
  nodes: Node[];
  edges: Edge[];
} {
  const nodes = recipe.diagram!.nodes.map((node, index) => {
    const configuredLabel = typeof spec[node.label] === 'string' && spec[node.label] ? String(spec[node.label]) : node.label;
    return {
      id: node.id,
      data: { label: configuredLabel },
      position: {
        x: 100 + index * 220,
        y: 180
      },
      style: baseNodeStyle()
    } satisfies Node;
  });

  const edges = recipe.diagram!.edges.map((edge, index) => ({
    id: `edge-${index}-${edge.source}-${edge.target}`,
    source: edge.source,
    target: edge.target,
    label: edge.label,
    markerEnd: { type: MarkerType.ArrowClosed },
    style: { stroke: '#94a3b8' },
    labelStyle: { fill: '#cbd5e1', fontSize: 12 }
  }));

  return { nodes, edges };
}

function buildFallbackDiagram(
  recipe: RecipeDefinition | null,
  spec: ArchitectureSpec
): {
  nodes: Node[];
  edges: Edge[];
} {
  if (!recipe) {
    return {
      nodes: [{ id: 'empty', data: { label: 'Select a recipe to preview architecture' }, position: { x: 140, y: 180 }, style: baseNodeStyle() }],
      edges: []
    };
  }

  const recipeName = recipe?.name ?? '';
  const isTwoService = recipeName === 'two-service-rest' || ('serviceAName' in spec && 'serviceBName' in spec);
  if (isTwoService) {
    const serviceA = toStringOrDefault(spec.serviceAName, 'service-a');
    const serviceB = toStringOrDefault(spec.serviceBName, 'service-b');
    return {
      nodes: [
        { id: 'serviceA', data: { label: serviceA }, position: { x: 100, y: 180 }, style: baseNodeStyle() },
        { id: 'serviceB', data: { label: serviceB }, position: { x: 360, y: 180 }, style: baseNodeStyle() }
      ],
      edges: [
        {
          id: 'serviceA-serviceB',
          source: 'serviceA',
          target: 'serviceB',
          label: 'REST',
          markerEnd: { type: MarkerType.ArrowClosed },
          style: { stroke: '#94a3b8' },
          labelStyle: { fill: '#cbd5e1', fontSize: 12 }
        }
      ]
    };
  }

  const serviceName = toStringOrDefault(spec.serviceName, 'service');
  const databaseLabel = recipeName.includes('postgres') ? 'postgres' : 'h2';
  return {
    nodes: [
      { id: 'service', data: { label: serviceName }, position: { x: 100, y: 180 }, style: baseNodeStyle() },
      { id: 'database', data: { label: databaseLabel }, position: { x: 360, y: 180 }, style: baseNodeStyle() }
    ],
    edges: [
      {
        id: 'service-database',
        source: 'service',
        target: 'database',
        label: 'JDBC',
        markerEnd: { type: MarkerType.ArrowClosed },
        style: { stroke: '#94a3b8' },
        labelStyle: { fill: '#cbd5e1', fontSize: 12 }
      }
    ]
  };
}

function toStringOrDefault(value: unknown, fallback: string): string {
  return typeof value === 'string' && value.trim().length > 0 ? value : fallback;
}

function baseNodeStyle() {
  return {
    background: '#111827',
    color: '#e2e8f0',
    border: '1px solid #334155',
    borderRadius: '8px',
    width: 170,
    padding: '8px 10px'
  };
}
