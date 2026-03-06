export type RecipeInput = {
  name: string;
  type: string;
  required: boolean;
};

export type DiagramNode = {
  id: string;
  label: string;
};

export type DiagramEdge = {
  source: string;
  target: string;
  label?: string;
};

export type DiagramDefinition = {
  nodes: DiagramNode[];
  edges: DiagramEdge[];
};

export type RecipeDefinition = {
  name: string;
  description: string;
  inputs: RecipeInput[];
  diagram?: DiagramDefinition;
};

export type EntityField = {
  name: string;
  type: string;
};

export type EntityConfig = {
  name: string;
  fields: EntityField[];
};

export type ArchitectureSpec = {
  recipe: string;
  serviceName?: string;
  serviceAName?: string;
  serviceBName?: string;
  entities?: EntityConfig[];
  communication?: string;
  database?: string;
  [key: string]: unknown;
};
