package knightminer.ceramics.datagen;

import knightminer.ceramics.Ceramics;
import knightminer.ceramics.Registration;
import knightminer.ceramics.blocks.RainbowPorcelain;
import knightminer.ceramics.recipe.CeramicsTags;
import knightminer.ceramics.registration.BuildingBlockObject;
import knightminer.ceramics.registration.EnumBlockObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.data.SingleItemRecipeBuilder;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

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
    // recoloring terracotta
    ICriterionInstance terracottaCriteria = hasItem(CeramicsTags.Items.COLORED_TERRACOTTA);
    eachEnum(Registration.TERRACOTTA, DyeColor.values(), (item, color) -> {
      ShapedRecipeBuilder.shapedRecipe(item, 8)
                         .key('B', CeramicsTags.Items.COLORED_TERRACOTTA)
                         .key('D', color.getTag())
                         .setGroup("stained_terracotta")
                         .patternLine("BBB")
                         .patternLine("BDB")
                         .patternLine("BBB")
                         .addCriterion("has_terracotta", terracottaCriteria)
                         .build(consumer, location(item.getRegistryName().getPath() + "_recolor"));
    });

    // crafting porcelain
    ShapelessRecipeBuilder.shapelessRecipe(Registration.UNFIRED_PORCELAIN, 4)
                          .addIngredient(Tags.Items.GEMS_QUARTZ)
                          .addIngredient(Items.CLAY_BALL)
                          .addIngredient(Items.CLAY_BALL)
                          .addIngredient(Items.CLAY_BALL)
                          .addCriterion("has_quartz", hasItem(Tags.Items.GEMS_QUARTZ))
                          .build(consumer);

    // unfired porcelain
    ShapedRecipeBuilder.shapedRecipe(Registration.UNFIRED_PORCELAIN_BLOCK)
                       .key('b', Registration.UNFIRED_PORCELAIN)
                       .patternLine("bb")
                       .patternLine("bb")
                       .addCriterion("has_item", hasItem(Registration.UNFIRED_PORCELAIN))
                       .build(consumer);
    // smelting porcelain
    Item porcelainBlock = Registration.PORCELAIN_BLOCK.asItem(DyeColor.WHITE);
    CookingRecipeBuilder.smeltingRecipe(
        Ingredient.fromItems(Registration.UNFIRED_PORCELAIN_BLOCK),
        porcelainBlock, 0.1f, 200)
                        .addCriterion("has_item", hasItem(Registration.UNFIRED_PORCELAIN_BLOCK))
                        .build(consumer, suffix(porcelainBlock, "_smelting"));
    // colored porcelain
    ICriterionInstance porcelainCriteria = hasItem(Registration.PORCELAIN_BLOCK.asItem(DyeColor.WHITE));
    eachEnum(Registration.PORCELAIN_BLOCK, DyeColor.values(), (item, color) -> {
      ShapedRecipeBuilder.shapedRecipe(item, 8)
                         .key('B', CeramicsTags.Items.PORCELAIN)
                         .key('D', color.getTag())
                         .setGroup(locationString("dye_porcelain"))
                         .patternLine("BBB")
                         .patternLine("BDB")
                         .patternLine("BBB")
                         .addCriterion("has_porcelain", porcelainCriteria)
                         .build(consumer);
    });
    // rainbow porcelain
    CookingRecipeBuilder.smeltingRecipe(
        Ingredient.fromTag(CeramicsTags.Items.COLORED_PORCELAIN),
        Registration.RAINBOW_PORCELAIN.asItem(RainbowPorcelain.RED),
        0.1f, 200)
                        .addCriterion("has_porcelain", hasItem(
                            ItemPredicate.Builder.create()
                                                 .tag(CeramicsTags.Items.COLORED_PORCELAIN)
                                                 .build()))
                        .build(consumer, location("rainbow_porcelain"));
    // smelt for full rainbow
    ICriterionInstance hasTheRainbow = hasItem(CeramicsTags.Items.RAINBOW_PORCELAIN);
    eachEnum(Registration.RAINBOW_PORCELAIN, RainbowPorcelain.values(), (item, color) -> {
      SingleItemRecipeBuilder.stonecuttingRecipe(
          Ingredient.fromTag(CeramicsTags.Items.RAINBOW_PORCELAIN),
          item)
                             .addCriterion("has_the_rainbow", hasTheRainbow)
                             .build(consumer, item.getRegistryName());
    });

    // bricks
    // vanilla brick block shortcuts
    ICriterionInstance hasClayBrick = hasItem(Items.BRICK);
    ShapedRecipeBuilder.shapedRecipe(Items.BRICK_SLAB)
                       .key('b', Items.BRICK)
                       .patternLine("bb")
                       .addCriterion("has_bricks", hasClayBrick)
                       .setGroup(Items.BRICK_SLAB.getRegistryName().getPath())
                       .build(consumer, location("brick_slab_from_bricks"));
    // stairs shortcut
    ShapedRecipeBuilder.shapedRecipe(Items.BRICK_STAIRS)
                       .key('b', Items.BRICK)
                       .patternLine("b  ")
                       .patternLine("bb ")
                       .patternLine("bbb")
                       .addCriterion("has_bricks", hasClayBrick)
                       .setGroup(Items.BRICK_STAIRS.getRegistryName().getPath())
                       .build(consumer, location("brick_stairs_from_bricks"));
    // block from slab
    ShapedRecipeBuilder.shapedRecipe(Items.BRICKS)
                       .key('B', Items.BRICK_SLAB)
                       .patternLine("B")
                       .patternLine("B")
                       .addCriterion("has_item", hasItem(Items.BRICK_SLAB))
                       .setGroup(Items.BRICKS.getRegistryName().getPath())
                       .build(consumer, location("bricks_from_slab"));

    // dark bricks from smelting bricks
    eachBuilding(BRICKS, Registration.DARK_BRICKS, (input, output) -> {
      CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(input), output, 0.1f, 200)
                          .addCriterion("has_item", hasItem(input))
                          .build(consumer, suffix(output, "_smelting"));
    });
    registerSlabStairWall(consumer, Registration.DARK_BRICKS);

    // magma bricks from lava bucket
    ICriterionInstance hasLava = hasItem(Items.LAVA_BUCKET);
    addBrickRecipe(consumer, BRICKS, Items.LAVA_BUCKET, Registration.LAVA_BRICKS, "lava");
    registerSlabStairWall(consumer, Registration.LAVA_BRICKS);

    // dragon bricks from dragon's breath
    addBrickRecipe(consumer, BRICKS, Items.DRAGON_BREATH, Registration.DRAGON_BRICKS, "dragon");
    registerSlabStairWall(consumer, Registration.DRAGON_BRICKS);

    // porcelain bricks
    CookingRecipeBuilder.smeltingRecipe(
        Ingredient.fromItems(Registration.UNFIRED_PORCELAIN),
        Registration.PORCELAIN_BRICK,
        0.1f, 200)
                        .addCriterion("has_item", hasItem(Registration.UNFIRED_PORCELAIN))
                        .build(consumer);
    ICriterionInstance hasBricks = hasItem(Registration.PORCELAIN_BRICK);
    ShapedRecipeBuilder.shapedRecipe(Registration.PORCELAIN_BRICKS)
                       .key('b', Registration.PORCELAIN_BRICK)
                       .patternLine("bb")
                       .patternLine("bb")
                       .addCriterion("has_bricks", hasBricks)
                       .setGroup(Registration.PORCELAIN_BRICK.getRegistryName().toString())
                       .build(consumer);
    // slab shortcut
    Item porcelainSlab = Registration.PORCELAIN_BRICKS.getSlabItem();
    ShapedRecipeBuilder.shapedRecipe(porcelainSlab)
                       .key('b', Registration.PORCELAIN_BRICK)
                       .patternLine("bb")
                       .addCriterion("has_bricks", hasBricks)
                       .setGroup(porcelainSlab.getRegistryName().toString())
                       .build(consumer, suffix(porcelainSlab, "_from_bricks"));
    // stairs shortcut
    Item porcelainStairs = Registration.PORCELAIN_BRICKS.getStairsItem();
    ShapedRecipeBuilder.shapedRecipe(porcelainStairs)
                       .key('b', Registration.PORCELAIN_BRICK)
                       .patternLine("b  ")
                       .patternLine("bb ")
                       .patternLine("bbb")
                       .addCriterion("has_bricks", hasBricks)
                       .setGroup(porcelainStairs.getRegistryName().toString())
                       .build(consumer, suffix(porcelainStairs, "_from_bricks"));
    registerSlabStairWall(consumer, Registration.PORCELAIN_BRICKS);

    // golden bricks
    addBrickRecipe(consumer, Registration.PORCELAIN_BRICKS, Items.GOLD_NUGGET, Registration.GOLDEN_BRICKS, "gold");
    registerSlabStairWall(consumer, Registration.GOLDEN_BRICKS);

    // marine bricks
    addBrickRecipe(consumer, Registration.PORCELAIN_BRICKS, Items.PRISMARINE_SHARD, Registration.MARINE_BRICKS, "prismarine");
    registerSlabStairWall(consumer, Registration.MARINE_BRICKS);

    // monochrome uses ink, not black dye intentionally
    addBrickRecipe(consumer, Registration.PORCELAIN_BRICKS, Items.INK_SAC, Registration.MONOCHROME_BRICKS, "ink");
    registerSlabStairWall(consumer, Registration.MONOCHROME_BRICKS);

    // rainbow
    eachBuilding(Registration.PORCELAIN_BRICKS, Registration.RAINBOW_BRICKS, (input, output) -> {
      CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(input), output, 0.1f, 200)
                          .addCriterion("has_item", hasItem(input))
                          .build(consumer, suffix(output, "_smelting"));
    });
    registerSlabStairWall(consumer, Registration.RAINBOW_BRICKS);

    // buckets
    // unfired
    ShapedRecipeBuilder.shapedRecipe(Registration.UNFIRED_CLAY_BUCKET)
                       .key('c', Items.CLAY_BALL)
                       .patternLine("c c")
                       .patternLine(" c ")
                       .addCriterion("has_clay", hasItem(Items.CLAY_BALL))
                       .build(consumer);
    // fired
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Registration.UNFIRED_CLAY_BUCKET), Registration.CLAY_BUCKET, 0.3f, 200)
                        .addCriterion("has_clay", hasItem(Registration.UNFIRED_CLAY_BUCKET))
                        .build(consumer);

    // armor
    // clay plates
    ShapedRecipeBuilder.shapedRecipe(Registration.UNFIRED_CLAY_PLATE)
                       .key('c', Items.CLAY_BALL)
                       .patternLine("cc")
                       .addCriterion("has_clay", hasItem(Items.CLAY_BALL))
                       .build(consumer);
    CookingRecipeBuilder.smeltingRecipe(
        Ingredient.fromItems(Registration.UNFIRED_CLAY_PLATE),
        Registration.CLAY_PLATE,
        0.1f, 200)
                        .addCriterion("has_clay_plate", hasItem(Registration.UNFIRED_CLAY_PLATE))
                        .build(consumer);
    // helmet
    ICriterionInstance hasClayPlate = hasItem(Registration.CLAY_PLATE);
    ShapedRecipeBuilder.shapedRecipe(Registration.CLAY_HELMET)
                       .key('c', Registration.CLAY_PLATE)
                       .patternLine("ccc")
                       .patternLine("c c")
                       .addCriterion("has_plate", hasClayPlate)
                       .build(consumer);
    // chestplate
    ShapedRecipeBuilder.shapedRecipe(Registration.CLAY_CHESTPLATE)
                       .key('c', Registration.CLAY_PLATE)
                       .patternLine("c c")
                       .patternLine("ccc")
                       .patternLine("ccc")
                       .addCriterion("has_plate", hasClayPlate)
                       .build(consumer);
    // leggings
    ShapedRecipeBuilder.shapedRecipe(Registration.CLAY_LEGGINGS)
                       .key('c', Registration.CLAY_PLATE)
                       .patternLine("ccc")
                       .patternLine("c c")
                       .patternLine("c c")
                       .addCriterion("has_plate", hasClayPlate)
                       .build(consumer);
    // boots
    ShapedRecipeBuilder.shapedRecipe(Registration.CLAY_BOOTS)
                       .key('c', Registration.CLAY_PLATE)
                       .patternLine("c c")
                       .patternLine("c c")
                       .addCriterion("has_plate", hasClayPlate)
                       .build(consumer);

    // clay uncrafting
    ShapelessRecipeBuilder.shapelessRecipe(Items.CLAY_BALL, 4)
                          .addIngredient(Items.CLAY)
                          .addCriterion("has_unfired", hasItem(Items.CLAY))
                          .setGroup(locationString("clay_uncrafting"))
                          .build(consumer, location("clay_uncrafting"));
    ShapelessRecipeBuilder.shapelessRecipe(Items.CLAY_BALL, 3)
                          .addIngredient(Registration.UNFIRED_CLAY_BUCKET)
                          .addCriterion("has_unfired", hasItem(Registration.UNFIRED_CLAY_BUCKET))
                          .setGroup(locationString("clay_uncrafting"))
                          .build(consumer, suffix(Registration.UNFIRED_CLAY_BUCKET, "_uncrafting"));
    ShapelessRecipeBuilder.shapelessRecipe(Items.CLAY_BALL, 2)
                          .addIngredient(Registration.UNFIRED_CLAY_PLATE)
                          .addCriterion("has_unfired", hasItem(Registration.UNFIRED_CLAY_PLATE))
                          .setGroup(locationString("clay_uncrafting"))
                          .build(consumer, suffix(Registration.UNFIRED_CLAY_PLATE, "_uncrafting"));
    // porcelain uncrafting
    ShapelessRecipeBuilder.shapelessRecipe(Registration.UNFIRED_PORCELAIN, 4)
                          .addIngredient(Registration.UNFIRED_PORCELAIN_BLOCK)
                          .addCriterion("has_unfired", hasItem(Registration.UNFIRED_PORCELAIN_BLOCK))
                          .setGroup(locationString("porcelain_uncrafting"))
                          .build(consumer, suffix(Registration.UNFIRED_PORCELAIN_BLOCK, "_uncrafting"));
    // compat, wish there was a better way to do this
    ShapedRecipeBuilder.shapedRecipe(Blocks.CAKE)
                       .key('M', CeramicsTags.Items.MILK_BUCKETS)
                       .key('S', Items.SUGAR)
                       .key('W', Items.WHEAT)
                       .key('E', Items.EGG)
                       .patternLine("MMM")
                       .patternLine("SES")
                       .patternLine("WWW")
                       .addCriterion("has_egg", this.hasItem(Items.EGG))
                       .build(consumer, location("cake"));
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
