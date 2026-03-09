import { RecipeDefinition } from '../types';

type RecipeSelectorProps = {
  recipes: RecipeDefinition[];
  selectedRecipe: string;
  loadingRecipes: boolean;
  onSelect: (recipeName: string) => void;
};

export default function RecipeSelector({ recipes, selectedRecipe, loadingRecipes, onSelect }: RecipeSelectorProps) {
  return (
    <section className="space-y-3 rounded-xl border border-slate-700/80 bg-panel/90 p-4">
      <h2 className="text-lg font-semibold">Select Recipe</h2>
      <p
        className={`rounded-md border px-3 py-2 text-sm ${
          loadingRecipes
            ? 'border-amber-400/60 bg-amber-400/15 text-amber-200'
            : 'border-amber-500/35 bg-amber-500/10 text-amber-100'
        }`}
      >
        Free-tier backend: recipe loading can take up to 1 minute. If recipes do not appear, try refresh.
      </p>
      <div className="grid gap-3">
        {recipes.map((recipe) => {
          const selected = recipe.name === selectedRecipe;
          return (
            <button
              key={recipe.name}
              type="button"
              onClick={() => onSelect(recipe.name)}
              className={`rounded-lg border p-4 text-left transition ${
                selected
                  ? 'border-accent bg-accent/10'
                  : 'border-slate-700 bg-slate-950/50 hover:border-slate-500 hover:bg-slate-950/80'
              }`}
            >
              <h3 className="font-semibold text-white">{recipe.name}</h3>
              <p className="mt-1 text-sm text-slate-300">{recipe.description}</p>
              <p className="mt-3 text-xs text-slate-400">{recipePreviewText(recipe)}</p>
            </button>
          );
        })}
      </div>
    </section>
  );
}

function recipePreviewText(recipe: RecipeDefinition): string {
  if (recipe.diagram && recipe.diagram.nodes.length > 0) {
    const names = recipe.diagram.nodes.map((node) => node.label).join(' -> ');
    return `Preview: ${names}`;
  }

  if (recipe.name === 'two-service-rest') {
    return 'Preview: serviceA -> serviceB';
  }

  return recipe.name.includes('postgres') ? 'Preview: service -> postgres' : 'Preview: service -> h2';
}
