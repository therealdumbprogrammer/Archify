export type RecipeInput = {
  name: string;
  type: string;
  required: boolean;
};

export type RecipeDefinition = {
  name: string;
  description: string;
  inputs: RecipeInput[];
};

export type EntityField = {
  name: string;
  type: string;
};

export type EntityConfig = {
  name: string;
  fields: EntityField[];
};
