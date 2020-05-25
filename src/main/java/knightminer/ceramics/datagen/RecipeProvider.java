package knightminer.ceramics.datagen;

import knightminer.ceramics.Ceramics;
import knightminer.ceramics.registration.BuildingBlockObject;
import knightminer.ceramics.registration.EnumBlockObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.SingleItemRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("ConstantConditions")
public class RecipeProvider extends net.minecraft.data.RecipeProvider {

  /** Vanilla bricks as a building block object */
  private static final BuildingBlockObject BRICKS = BuildingBlockObject.fromBlocks(Blocks.BRICKS, Blocks.BRICK_SLAB, Blocks.BRICK_STAIRS, Blocks.BRICK_WALL);

  public RecipeProvider(DataGenerator gen) {
    super(gen);
  }

  @Nonnull
  @Override
  public String getName() {
    return "Ceramics Recipes";
  }

  @Override
  protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
  }


  /* Location helpers */

  /**
   * Gets a resource location for Ceramics
   * @param id  Location path
   * @return  Location for Ceramics
   */
  private static ResourceLocation location(String id) {
    return new ResourceLocation(Ceramics.MOD_ID, id);
  }

  /**
   * Gets a resource location as a string for Ceramics
   * @param id  Location path
   * @return  String location for Ceramics
   */
  private static String locationString(String id) {
    return Ceramics.MOD_ID + ":" + id;
  }

  /**
   * Suffixes the resource location path with the given value
   * @param loc     Location to suffix
   * @param suffix  Suffix value
   * @return  Resource location path
   */
  private static ResourceLocation suffix(ResourceLocation loc, String suffix) {
    return new ResourceLocation(loc.getNamespace(), loc.getPath() + suffix);
  }

  /**
   * Suffixes the item's resource location with the given value
   * @param item    Item to suffix
   * @param suffix  Suffix value
   * @return  Resource location path
   */
  private static ResourceLocation suffix(IItemProvider item, String suffix) {
    return suffix(item.asItem().getRegistryName(), suffix);
  }


  /* Iteration helpers */

  /**
   * Loops over values in an enum
   * @param enumBlock  Enum block instance
   * @param values     List of values to iterate
   * @param consumer   Logic to run for each recipe, with item as a parameter
   * @param <T>        Enum type
   */
  private <T extends Enum<T>> void eachEnum(EnumBlockObject<T,? extends Block> enumBlock, T[] values, BiConsumer<Item,T> consumer) {
    for(T value : values) {
      consumer.accept(enumBlock.asItem(value), value);
    }
  }

  /**
   * Runs the consumer once for each of block, slab, stairs, and wall
   * @param input      Recipe input for consumer
   * @param output     Recipe output for consumer
   * @param consumer   Consumer to create recipes
   */
  private void eachBuilding(BuildingBlockObject input, BuildingBlockObject output, BiConsumer<Item,Item> consumer) {
    consumer.accept(input.asItem(), output.asItem());
    consumer.accept(input.getSlabItem(), output.getSlabItem());
    consumer.accept(input.getStairsItem(), output.getStairsItem());
    consumer.accept(input.getWallItem(), output.getWallItem());
  }

  /**
   * Registers generic building block recipes
   * @param consumer  Recipe consumer
   * @param building  Building object instance
   */
  private void registerSlabStairWall(@Nonnull Consumer<IFinishedRecipe> consumer, BuildingBlockObject building) {
    Item item = building.asItem();
    ResourceLocation location = item.getRegistryName();
    ICriterionInstance hasBuilding = hasItem(ItemPredicate.Builder.create().item(item).build());
    Ingredient ingredient = Ingredient.fromItems(item);

    // slab
    Item slab = building.getSlabItem();
    ShapedRecipeBuilder.shapedRecipe(slab, 6)
        .key('B', item)
        .patternLine("BBB")
        .addCriterion("has_item", hasBuilding)
        .setGroup(slab.getRegistryName().toString())
        .build(consumer, suffix(location, "_slab_crafting"));
    SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, slab, 2)
                           .addCriterion("has_item", hasBuilding)
                           .build(consumer, suffix(location, "_slab_stonecutter"));

    // stairs
    Item stairs = building.getStairsItem();
    ShapedRecipeBuilder.shapedRecipe(stairs, 4)
        .key('B', item)
        .patternLine("B  ")
        .patternLine("BB ")
        .patternLine("BBB")
        .addCriterion("has_item", hasBuilding)
        .setGroup(stairs.getRegistryName().toString())
        .build(consumer, suffix(location, "_stairs_crafting"));
    SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, stairs)
                           .addCriterion("has_item", hasBuilding)
                           .build(consumer, suffix(location, "_stairs_stonecutter"));

    // wall
    ShapedRecipeBuilder.shapedRecipe(building.getWallItem(), 6)
        .key('B', item)
        .patternLine("BBB")
        .patternLine("BBB")
        .addCriterion("has_item", hasBuilding)
        .build(consumer, suffix(location, "_wall_crafting"));
    SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, building.getWallItem())
                           .addCriterion("has_item", hasBuilding)
                           .build(consumer, suffix(location, "_wall_stonecutter"));

    // block from slab, its bricks so its easy
    ShapedRecipeBuilder.shapedRecipe(item)
                       .key('B', slab)
                       .patternLine("B")
                       .patternLine("B")
                       .addCriterion("has_item", hasItem(slab))
                       .setGroup(location.toString())
                       .build(consumer, suffix(location, "_from_slab"));
  }

  /**
   * Add recipes for surrouding an ingredient with a brick to get another brick
   * @param consumer    Recipe consumer
   * @param from        Input bricks
   * @param ingredient  Ingredient to transform
   * @param to          Output bricks
   * @param name        Recipe name
   */
  private void addBrickRecipe(@Nonnull Consumer<IFinishedRecipe> consumer, BuildingBlockObject from, Item ingredient, BuildingBlockObject to, String name) {
    ICriterionInstance criteria = hasItem(ingredient);
    eachBuilding(from, to, (input, output) -> {
      ShapedRecipeBuilder.shapedRecipe(output, 8)
                         .key('B', input)
                         .key('i', ingredient)
                         .patternLine("BBB")
                         .patternLine("BiB")
                         .patternLine("BBB")
                         .addCriterion("has_" + name, criteria)
                         .setGroup(output.getRegistryName().toString())
                         .build(consumer, suffix(output, "_" + name));
    });
  }
}