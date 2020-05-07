package net.minecraftforge.common.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MetadataContainer extends BlockStateContainer {

    private final ImmutableMap<IBlockState, Integer> stateToMeta;

    public MetadataContainer(Block blockIn, IProperty<?>... properties) {
        super(blockIn, properties);
        stateToMeta = ImmutableMap.copyOf(getValidStates().stream()
                .collect(Collectors.toMap(key -> key, value -> getValidStates().indexOf(value))));
    }

    public int getMetaFromState(IBlockState state) {
        return stateToMeta.get(state);
    }

    public IBlockState getStateFromMeta(int meta) {
        return getValidStates().get(meta);
    }

    public static class Builder {
        private final int capacity;
        private final Block block;
        private List<IProperty<?>> properties = new ArrayList<>();
        private int currentPlace = 1;

        private Builder(Block block, int capacity) {
            this.capacity = capacity;
            this.block = block;
        }

        @Nonnull
        public static Builder create(@Nonnull Block block, int capacity) {
            return new Builder(block, capacity);
        }

        @Nonnull
        public static Builder create(Block block) {
            return new Builder(block, 16);
        }

        public Builder addProperty(@Nonnull IProperty<?> property) {
            canAddValues(property);
            properties.add(property);
            return this;
        }

        public MetadataContainer build() {
            return new MetadataContainer(block, properties.toArray(new IProperty<?>[0]));
        }

        private void canAddValues(@Nonnull IProperty prop) {
            int count = prop.getAllowedValues().size();
            boolean flag = (currentPlace * count) <= capacity;
            if (!flag)
                throw new IndexOutOfBoundsException("You can't put this " + prop.getName() + " of size " + prop.getAllowedValues().size() + " " +
                        "to the MetadataContainer.Builder, because free value is " + (capacity - currentPlace) + "!");
            currentPlace *= count;
        }
    }
}
