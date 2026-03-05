import { RecipeDefinition } from '../types';

type RecipeSelectorProps = {
  recipes: RecipeDefinition[];
  selectedRecipe: string;
  onSelect: (recipeName: string) => void;
};

export default function RecipeSelector({ recipes, selectedRecipe, onSelect }: RecipeSelectorProps) {
  return (
    <section className="rounded-xl border border-slate-700 bg-panel/90 p-4">
      <h2 className="mb-3 text-lg font-semibold">Recipe</h2>
      <select
        className="w-full rounded-md border border-slate-600 bg-slate-900 px-3 py-2"
        value={selectedRecipe}
        onChange={(event) => onSelect(event.target.value)}
      >
        <option value="">Select a recipe</option>
        {recipes.map((recipe) => (
          <option key={recipe.name} value={recipe.name}>
            {recipe.name} - {recipe.description}
          </option>
        ))}
      </select>
    </section>
  );
}
