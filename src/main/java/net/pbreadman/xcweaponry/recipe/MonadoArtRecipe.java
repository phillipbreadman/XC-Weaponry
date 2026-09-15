package net.pbreadman.xcweaponry.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.pbreadman.xcweaponry.items.custom.ModDataComponents;

public class MonadoArtRecipe implements SmithingRecipe {
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public MonadoArtRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    private static DataComponentType<?> resolveUnlock(HolderLookup.Provider registries, ItemStack addition) {
        ResourceLocation id = addition.get(ModDataComponents.UNLOCKS_COMPONENT.get());
        if (id == null) return null;
        return registries
                .lookupOrThrow(Registries.DATA_COMPONENT_TYPE)
                .get(ResourceKey.create(Registries.DATA_COMPONENT_TYPE, id))
                .map(Holder.Reference::value)
                .orElse(null);
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        if (!this.template.test(input.template()) || !this.base.test(input.base()) || !this.addition.test(input.addition())) {
            return false;
        }
        DataComponentType<?> unlock = resolveUnlock(level.registryAccess(), input.addition());
        return unlock != null && !input.base().has(unlock);
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack itemstack = input.base().copy();
        DataComponentType<?> unlock = resolveUnlock(registries, input.addition());
        if (unlock != null) {
            itemstack.set(castUnitComponent(unlock), Unit.INSTANCE);
        }
        return itemstack;
    }

    @SuppressWarnings("unchecked")
    private static DataComponentType<Unit> castUnitComponent(DataComponentType<?> type) {
        return (DataComponentType<Unit>) type;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return this.template.test(stack);
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return this.addition.test(stack);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.MONADO_ART.get();
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of(this.template, this.base, this.addition).anyMatch(Ingredient::hasNoItems);
    }

    public static class Serializer implements RecipeSerializer<MonadoArtRecipe> {
        private static final MapCodec<MonadoArtRecipe> CODEC = RecordCodecBuilder.mapCodec(
            p_345024_ -> p_345024_.group(
                        Ingredient.CODEC.fieldOf("template").forGetter(p_301310_ -> p_301310_.template),
                        Ingredient.CODEC.fieldOf("base").forGetter(p_300938_ -> p_300938_.base),
                        Ingredient.CODEC.fieldOf("addition").forGetter(p_301153_ -> p_301153_.addition),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(p_300935_ -> p_300935_.result)
                    )
                    .apply(p_345024_, MonadoArtRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MonadoArtRecipe> STREAM_CODEC = StreamCodec.of(
            MonadoArtRecipe.Serializer::toNetwork, MonadoArtRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<MonadoArtRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MonadoArtRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static MonadoArtRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient ingredient1 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient ingredient2 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            return new MonadoArtRecipe(ingredient, ingredient1, ingredient2, itemstack);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, MonadoArtRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.template);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.base);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.addition);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        }
    }
}