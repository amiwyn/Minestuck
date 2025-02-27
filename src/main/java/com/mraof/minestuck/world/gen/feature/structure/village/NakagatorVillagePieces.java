package com.mraof.minestuck.world.gen.feature.structure.village;

import com.mraof.minestuck.entity.consort.EnumConsort;
import com.mraof.minestuck.world.gen.feature.MSStructurePieces;
import com.mraof.minestuck.world.gen.feature.structure.blocks.StructureBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

public class NakagatorVillagePieces
{
	public static class RadioTowerCenter extends ConsortVillageCenter.VillageCenter
	{
		public RadioTowerCenter(List<ConsortVillagePieces.PieceWeight> pieceWeightList, int x, int z, Random rand)
		{
			super(MSStructurePieces.RADIO_TOWER_CENTER, pieceWeightList, 0, 0);
			this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(rand));
			
			this.boundingBox = new MutableBoundingBox(x, 64, z, x + 8 - 1, 90, z + 8 - 1);
		}
		
		public RadioTowerCenter(TemplateManager templates, CompoundNBT nbt)
		{
			super(MSStructurePieces.TURTLE_WELL_CENTER, nbt, 0);
		}
		
		@Override
		public void addChildren(StructurePiece componentIn, List<StructurePiece> listIn, Random rand)
		{
			ConsortVillagePieces.generateAndAddRoadPiece((ConsortVillageCenter.VillageCenter) componentIn, listIn, rand, boundingBox.x0 + 3, boundingBox.y0, boundingBox.z1 + 1, Direction.SOUTH);
			ConsortVillagePieces.generateAndAddRoadPiece((ConsortVillageCenter.VillageCenter) componentIn, listIn, rand, boundingBox.x0 - 1, boundingBox.y0, boundingBox.z0 + 3, Direction.WEST);
			ConsortVillagePieces.generateAndAddRoadPiece((ConsortVillageCenter.VillageCenter) componentIn, listIn, rand, boundingBox.x0 + 3, boundingBox.y0, boundingBox.z0 - 1, Direction.NORTH);
			ConsortVillagePieces.generateAndAddRoadPiece((ConsortVillageCenter.VillageCenter) componentIn, listIn, rand, boundingBox.x1 + 1, boundingBox.y0, boundingBox.z0 + 3, Direction.EAST);
		}

		@Override
		public boolean postProcess(ISeedReader worldIn, StructureManager manager, ChunkGenerator chunkGeneratorIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn, BlockPos pos)
		{
			if (this.averageGroundLvl < 0)
			{
				this.averageGroundLvl = this.getAverageGroundLevel(worldIn, chunkGeneratorIn, structureBoundingBoxIn);

				if (this.averageGroundLvl < 0)
				{
					return true;
				}

				this.boundingBox.move(0, this.averageGroundLvl - this.boundingBox.y0 - 1, 0);
			}

			StructureBlockRegistry blocks = StructureBlockRegistry.getOrDefault(chunkGeneratorIn);
			BlockState secondary = blocks.getBlockState("structure_secondary");
			BlockState secondaryDecor = blocks.getBlockState("structure_secondary_decorative");
			BlockState fence = blocks.getBlockState("village_fence");
			BlockState topBlock = Blocks.QUARTZ_BLOCK.defaultBlockState();
			BlockState topSlab0 = Blocks.QUARTZ_SLAB.defaultBlockState();
			BlockState topSlab1 = Blocks.QUARTZ_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP);

			this.generateAirBox(worldIn, structureBoundingBoxIn, 1, 1, 1, 6, 6, 6);
			this.generateAirBox(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0);
			this.generateAirBox(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7);
			this.generateAirBox(worldIn, structureBoundingBoxIn, 0, 1, 2, 0, 2, 5);
			this.generateAirBox(worldIn, structureBoundingBoxIn, 7, 1, 2, 7, 2, 5);

			this.generateBox(worldIn, structureBoundingBoxIn, 0, 0, 0, 7, 0, 7, secondary, secondary, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 2, 0, 2, 5, 0, 5, secondaryDecor, secondaryDecor, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 3, 0, 1, 4, 0, 1, secondaryDecor, secondaryDecor, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 3, 0, 6, 4, 0, 6, secondaryDecor, secondaryDecor, false);

			this.generateBox(worldIn, structureBoundingBoxIn, 0, 1, 0, 2, 7, 0, secondary, secondary, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 3, 3, 0, 4, 7, 0, secondary, secondary, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 5, 1, 0, 7, 7, 0, secondary, secondary, false);

			this.generateBox(worldIn, structureBoundingBoxIn, 0, 1, 7, 2, 7, 7, secondary, secondary, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 3, 3, 7, 4, 7, 7, secondary, secondary, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 5, 1, 7, 7, 7, 7, secondary, secondary, false);

			this.placeBlock(worldIn, secondary, 0, 1, 1, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 0, 1, 6, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 0, 2, 1, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 0, 2, 6, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 0, 3, 2, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 0, 3, 5, structureBoundingBoxIn);
			this.generateBox(worldIn, structureBoundingBoxIn, 0, 4, 3, 0, 5, 4, secondary, secondary, false);
			this.placeBlock(worldIn, secondary, 0, 6, 2, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 0, 6, 5, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 0, 7, 1, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 0, 7, 6, structureBoundingBoxIn);

			this.placeBlock(worldIn, secondary, 7, 1, 1, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 7, 1, 6, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 7, 2, 1, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 7, 2, 6, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 7, 3, 2, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 7, 3, 5, structureBoundingBoxIn);
			this.generateBox(worldIn, structureBoundingBoxIn, 7, 4, 3, 7, 5, 4, secondary, secondary, false);
			this.placeBlock(worldIn, secondary, 7, 6, 2, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 7, 6, 5, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 7, 7, 1, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 7, 7, 6, structureBoundingBoxIn);

			this.generateBox(worldIn, structureBoundingBoxIn, 1, 7, 1, 6, 7, 6, secondary, secondary, false);

			this.generateBox(worldIn, structureBoundingBoxIn, 1, 8, 1, 1, 13, 6, secondary, secondary, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 6, 8, 1, 6, 13, 6, secondary, secondary, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 2, 13, 2, 5, 13, 5, secondary, secondary, false);

			this.placeBlock(worldIn, secondary, 2, 9, 1, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 5, 9, 1, structureBoundingBoxIn);
			this.generateBox(worldIn, structureBoundingBoxIn, 3, 10, 1, 4, 11, 1, secondary, secondary, false);
			this.placeBlock(worldIn, secondary, 2, 12, 1, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 5, 12, 1, structureBoundingBoxIn);

			this.placeBlock(worldIn, secondary, 2, 9, 6, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 5, 9, 6, structureBoundingBoxIn);
			this.generateBox(worldIn, structureBoundingBoxIn, 3, 10, 6, 4, 11, 6, secondary, secondary, false);
			this.placeBlock(worldIn, secondary, 2, 12, 6, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 5, 12, 6, structureBoundingBoxIn);

			this.generateBox(worldIn, structureBoundingBoxIn, 1, 14, 1, 6, 19, 1, secondary, secondary, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 1, 14, 6, 6, 19, 6, secondary, secondary, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 2, 19, 2, 5, 20, 5, secondary, secondary, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 2, 20, 1, 5, 20, 1, secondary, secondary, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 2, 20, 6, 5, 20, 6, secondary, secondary, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 1, 20, 2, 1, 20, 5, secondary, secondary, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 6, 20, 2, 6, 20, 5, secondary, secondary, false);

			this.placeBlock(worldIn, secondary, 1, 15, 2, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 1, 15, 5, structureBoundingBoxIn);
			this.generateBox(worldIn, structureBoundingBoxIn, 1, 16, 3, 1, 17, 4, secondary, secondary, false);
			this.placeBlock(worldIn, secondary, 1, 18, 2, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 1, 18, 5, structureBoundingBoxIn);

			this.placeBlock(worldIn, secondary, 6, 15, 2, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 6, 15, 5, structureBoundingBoxIn);
			this.generateBox(worldIn, structureBoundingBoxIn, 6, 16, 3, 6, 17, 4, secondary, secondary, false);
			this.placeBlock(worldIn, secondary, 6, 18, 2, structureBoundingBoxIn);
			this.placeBlock(worldIn, secondary, 6, 18, 5, structureBoundingBoxIn);

			this.generateBox(worldIn, structureBoundingBoxIn, 3, 21, 3, 4, 22, 4, fence, fence, false);

			this.generateBox(worldIn, structureBoundingBoxIn, 3, 23, 3, 4, 23, 4, topBlock, topBlock, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 3, 24, 2, 4, 24, 2, topSlab0, topSlab0, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 3, 24, 5, 4, 24, 5, topSlab0, topSlab0, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 2, 24, 3, 2, 24, 4, topSlab0, topSlab0, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 5, 24, 3, 5, 24, 4, topSlab0, topSlab0, false);

			this.generateBox(worldIn, structureBoundingBoxIn, 3, 24, 1, 4, 24, 1, topSlab1, topSlab1, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 3, 24, 6, 4, 24, 6, topSlab1, topSlab1, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 1, 24, 3, 1, 24, 4, topSlab1, topSlab1, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 6, 24, 3, 6, 24, 4, topSlab1, topSlab1, false);
			this.placeBlock(worldIn, topSlab1, 2, 24, 2, structureBoundingBoxIn);
			this.placeBlock(worldIn, topSlab1, 2, 24, 5, structureBoundingBoxIn);
			this.placeBlock(worldIn, topSlab1, 5, 24, 2, structureBoundingBoxIn);
			this.placeBlock(worldIn, topSlab1, 5, 24, 5, structureBoundingBoxIn);
			this.generateBox(worldIn, structureBoundingBoxIn, 3, 25, 1, 4, 25, 1, topSlab0, topSlab0, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 3, 25, 6, 4, 25, 6, topSlab0, topSlab0, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 1, 25, 3, 1, 25, 4, topSlab0, topSlab0, false);
			this.generateBox(worldIn, structureBoundingBoxIn, 6, 25, 3, 6, 25, 4, topSlab0, topSlab0, false);
			this.placeBlock(worldIn, topSlab0, 2, 25, 2, structureBoundingBoxIn);
			this.placeBlock(worldIn, topSlab0, 2, 25, 5, structureBoundingBoxIn);
			this.placeBlock(worldIn, topSlab0, 5, 25, 2, structureBoundingBoxIn);
			this.placeBlock(worldIn, topSlab0, 5, 25, 5, structureBoundingBoxIn);

			return true;
		}
	}
	
	public static class HighNakHousing1 extends ConsortVillagePieces.ConsortVillagePiece
	{
		public HighNakHousing1(ConsortVillageCenter.VillageCenter start, Random rand, MutableBoundingBox boundingBox, Direction facing)
		{
			super(MSStructurePieces.HIGH_NAK_HOUSING_1, 0, 3);
			setOrientation(facing);
			this.boundingBox = boundingBox;
		}
		
		public HighNakHousing1(TemplateManager templates, CompoundNBT nbt)
		{
			super(MSStructurePieces.HIGH_NAK_HOUSING_1, nbt, 3);
		}
		
		public static HighNakHousing1 createPiece(ConsortVillageCenter.VillageCenter start, List<StructurePiece> componentList, Random rand, int x, int y, int z, Direction facing)
		{
			MutableBoundingBox structureboundingbox = MutableBoundingBox.orientBox(x, y, z, 0, 0, 0, 8, 13, 9, facing);
			return StructurePiece.findCollisionPiece(componentList, structureboundingbox) == null ? new HighNakHousing1(start, rand, structureboundingbox, facing) : null;
		}

		@Override
		public boolean postProcess(ISeedReader worldIn, StructureManager manager, ChunkGenerator chunkGeneratorIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn, BlockPos pos)
		{
			if (averageGroundLvl < 0)
			{
				averageGroundLvl = getAverageGroundLevel(worldIn, chunkGeneratorIn, structureBoundingBoxIn);

				if (averageGroundLvl < 0)
				{
					return true;
				}

				boundingBox.move(0, averageGroundLvl - boundingBox.y0 - 1, 0);
			}

			StructureBlockRegistry blocks = StructureBlockRegistry.getOrDefault(chunkGeneratorIn);
			BlockState buildBlock = blocks.getBlockState("structure_primary");
			BlockState stairs1 = blocks.getStairs("structure_primary_stairs", Direction.SOUTH, false);
			BlockState stairs2 = blocks.getStairs("structure_primary_stairs", Direction.NORTH, false);
			BlockState doorBlock = blocks.getBlockState("village_door");
			BlockState floorBlock = blocks.getBlockState("structure_secondary");
			BlockState torch = blocks.getBlockState("torch");
			BlockState wallTorch = blocks.getBlockState("wall_torch");

			//Floor
			generateBox(worldIn, structureBoundingBoxIn, 0, -1, 1, 7, 0, 8, floorBlock, floorBlock, false);

			//Base walls and second/third floors
			generateBox(worldIn, structureBoundingBoxIn, 0, 1, 1, 7, 12, 1, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 1, 8, 7, 12, 8, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 1, 2, 0, 12, 7, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 7, 1, 2, 7, 12, 7, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 1, 4, 2, 6, 4, 7, floorBlock, floorBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 1, 8, 2, 6, 8, 7, floorBlock, floorBlock, false);

			//Remove blocks in front of the building
			clearFront(worldIn, structureBoundingBoxIn, 2, 5, 1, 0);

			//First floor clear, doors, windows and furnishing
			generateAirBox(worldIn, structureBoundingBoxIn, 1, 1, 2, 6, 3, 7);
			generateDoor(worldIn, structureBoundingBoxIn, randomIn, 3, 1, 1, Direction.SOUTH, doorBlock.getBlock(), DoorHingeSide.RIGHT);
			generateDoor(worldIn, structureBoundingBoxIn, randomIn, 4, 1, 1, Direction.SOUTH, doorBlock.getBlock(), DoorHingeSide.LEFT);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 0, 2, 3, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 0, 2, 5, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 3, 2, 8, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 5, 2, 8, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 1, 1, 5, 2, 1, 5, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 1, 6, 3, 1, 7, buildBlock, buildBlock, false);

			//First to second floor stairs
			generateBox(worldIn, structureBoundingBoxIn, 6, 1, 4, 6, 1, 7, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 6, 2, 5, 6, 2, 7, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 6, 3, 6, 6, 3, 7, buildBlock, buildBlock, false);
			placeBlock(worldIn, stairs1, 6, 1, 3, structureBoundingBoxIn);
			placeBlock(worldIn, stairs1, 6, 2, 4, structureBoundingBoxIn);
			placeBlock(worldIn, stairs1, 6, 3, 5, structureBoundingBoxIn);
			placeBlock(worldIn, stairs1, 6, 4, 6, structureBoundingBoxIn);
			generateAirBox(worldIn, structureBoundingBoxIn, 6, 4, 2, 6, 4, 5);

			//Second floor windows
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 2, 6, 1, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 5, 6, 1, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 0, 6, 3, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 0, 6, 5, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 2, 6, 8, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 5, 6, 8, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 7, 6, 3, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 7, 6, 6, structureBoundingBoxIn);

			//Second to third floor stairs
			generateBox(worldIn, structureBoundingBoxIn, 1, 5, 2, 1, 5, 5, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 1, 6, 2, 1, 6, 4, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 1, 7, 2, 1, 7, 3, buildBlock, buildBlock, false);
			placeBlock(worldIn, stairs2, 1, 5, 6, structureBoundingBoxIn);
			placeBlock(worldIn, stairs2, 1, 6, 5, structureBoundingBoxIn);
			placeBlock(worldIn, stairs2, 1, 7, 4, structureBoundingBoxIn);
			placeBlock(worldIn, stairs2, 1, 8, 3, structureBoundingBoxIn);
			generateAirBox(worldIn, structureBoundingBoxIn, 1, 8, 4, 1, 8, 7);

			//Third floor windows
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 2, 10, 1, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 5, 10, 1, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 0, 10, 3, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 0, 10, 5, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 7, 10, 3, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 7, 10, 6, structureBoundingBoxIn);

			//Torches
			placeBlock(worldIn, wallTorch.setValue(WallTorchBlock.FACING, Direction.EAST), 1, 2, 4, structureBoundingBoxIn);
			placeBlock(worldIn, wallTorch.setValue(WallTorchBlock.FACING, Direction.NORTH), 4, 2, 7, structureBoundingBoxIn);
			placeBlock(worldIn, wallTorch.setValue(WallTorchBlock.FACING, Direction.EAST), 2, 6, 4, structureBoundingBoxIn);
			placeBlock(worldIn, torch, 4, 9, 5, structureBoundingBoxIn);

			//Consorts
			if(!spawns[0])
				spawns[0] = spawnConsort(2, 1, 6, structureBoundingBoxIn, worldIn, chunkGeneratorIn);
			if(!spawns[1])
				spawns[1] = spawnConsort(3, 5, 3, structureBoundingBoxIn, worldIn, chunkGeneratorIn);
			if(!spawns[2])
				spawns[2] = spawnConsort(5, 9, 6, structureBoundingBoxIn, worldIn, chunkGeneratorIn);

			return true;
		}
	}
	
	public static class HighNakMarket1 extends ConsortVillagePieces.ConsortVillagePiece
	{
		public HighNakMarket1(ConsortVillageCenter.VillageCenter start, Random rand, MutableBoundingBox boundingBox, Direction facing)
		{
			super(MSStructurePieces.HIGH_NAK_MARKET, 0, 3);
			setOrientation(facing);
			this.boundingBox = boundingBox;
		}
		
		public HighNakMarket1(TemplateManager templates, CompoundNBT nbt)
		{
			super(MSStructurePieces.HIGH_NAK_MARKET, nbt, 3);
		}
		
		public static HighNakMarket1 createPiece(ConsortVillageCenter.VillageCenter start, List<StructurePiece> componentList, Random rand, int x, int y, int z, Direction facing)
		{
			MutableBoundingBox structureboundingbox = MutableBoundingBox.orientBox(x, y, z, 0, 0, 0, 12, 14, 10, facing);
			return StructurePiece.findCollisionPiece(componentList, structureboundingbox) == null ? new HighNakMarket1(start, rand, structureboundingbox, facing) : null;
		}

		@Override
		public boolean postProcess(ISeedReader worldIn, StructureManager manager, ChunkGenerator chunkGeneratorIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn, BlockPos pos)
		{
			if (averageGroundLvl < 0)
			{
				averageGroundLvl = getAverageGroundLevel(worldIn, chunkGeneratorIn, structureBoundingBoxIn);

				if (averageGroundLvl < 0)
				{
					return true;
				}

				boundingBox.move(0, averageGroundLvl - boundingBox.y0 - 1, 0);
			}

			StructureBlockRegistry blocks = StructureBlockRegistry.getOrDefault(chunkGeneratorIn);
			BlockState buildBlock = blocks.getBlockState("structure_primary");
			BlockState stairs1 = blocks.getStairs("structure_primary_stairs", Direction.NORTH, false);
			BlockState stairs2 = blocks.getStairs("structure_primary_stairs", Direction.SOUTH, false);
			BlockState doorBlock = blocks.getBlockState("village_door");
			BlockState floorBlock = blocks.getBlockState("structure_secondary");
			BlockState fence = blocks.getBlockState("village_fence");
			BlockState torch = blocks.getBlockState("wall_torch");

			//Floor
			generateBox(worldIn, structureBoundingBoxIn, 3, -1, 1, 8, 0, 6, floorBlock, floorBlock, false);

			//Base walls and floors
			generateBox(worldIn, structureBoundingBoxIn, 2, -1, 7, 9, 13, 7, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 2, -1, 1, 2, 13, 6, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 9, -1, 1, 9, 13, 6, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 4, 2, 8, 4, 6, floorBlock, floorBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 8, 2, 8, 8, 6, floorBlock, floorBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 4, 1, 8, 4, 1, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 8, 1, 8, 8, 1, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 12, 2, 8, 12, 6, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 12, 1, 8, 13, 1, buildBlock, buildBlock, false);

			//Remove blocks in front of passages
			clearFront(worldIn, structureBoundingBoxIn, 2, 9, 1, 0);
			generateAirBox(worldIn, structureBoundingBoxIn, 10, 1, 8, 11, 4, 9);

			//Floor furnishing and doors
			generateAirBox(worldIn, structureBoundingBoxIn, 3, 1, 1, 8, 3, 6);
			generateBox(worldIn, structureBoundingBoxIn, 3, 1, 4, 8, 1, 4, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 5, 4, 8, 5, 4, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 5, 1, 8, 5, 1, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 9, 4, 8, 9, 4, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 9, 1, 8, 9, 1, fence, fence, false);
			generateDoor(worldIn, structureBoundingBoxIn, randomIn, 9, 5, 2, Direction.WEST, doorBlock.getBlock(), DoorHingeSide.LEFT);
			generateDoor(worldIn, structureBoundingBoxIn, randomIn, 2, 5, 2, Direction.EAST, doorBlock.getBlock(), DoorHingeSide.LEFT);
			generateDoor(worldIn, structureBoundingBoxIn, randomIn, 9, 9, 2, Direction.WEST, doorBlock.getBlock(), DoorHingeSide.LEFT);

			//Stairs 1
			generateBox(worldIn, structureBoundingBoxIn, 10, 4, 1, 11, 4, 4, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 11, 5, 1, 11, 5, 4, fence, fence, false);
			placeBlock(worldIn, fence, 10, 5, 1, structureBoundingBoxIn);
			placeBlock(worldIn, stairs1, 10, 1, 7, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 11, 1, 7, structureBoundingBoxIn);
			placeBlock(worldIn, fence, 11, 2, 7, structureBoundingBoxIn);
			placeBlock(worldIn, stairs1, 10, 2, 6, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 11, 2, 6, structureBoundingBoxIn);
			placeBlock(worldIn, fence, 11, 3, 6, structureBoundingBoxIn);
			placeBlock(worldIn, stairs1, 10, 3, 5, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 11, 3, 5, structureBoundingBoxIn);
			placeBlock(worldIn, fence, 11, 4, 5, structureBoundingBoxIn);
			placeBlock(worldIn, stairs1, 10, 4, 4, structureBoundingBoxIn);

			//Stairs 2
			generateBox(worldIn, structureBoundingBoxIn, 0, 4, 1, 1, 4, 3, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 5, 1, 0, 5, 3, fence, fence, false);
			placeBlock(worldIn, fence, 1, 5, 1, structureBoundingBoxIn);
			placeBlock(worldIn, stairs2, 1, 5, 4, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 0, 5, 4, structureBoundingBoxIn);
			placeBlock(worldIn, fence, 0, 6, 4, structureBoundingBoxIn);
			placeBlock(worldIn, stairs2, 1, 6, 5, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 0, 6, 5, structureBoundingBoxIn);
			placeBlock(worldIn, fence, 0, 7, 5, structureBoundingBoxIn);
			placeBlock(worldIn, stairs2, 1, 7, 6, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 0, 7, 6, structureBoundingBoxIn);
			placeBlock(worldIn, fence, 0, 8, 6, structureBoundingBoxIn);
			placeBlock(worldIn, stairs2, 1, 8, 7, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 0, 8, 7, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 0, 9, 7, 0, 9, 8, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 8, 8, 11, 8, 9, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 9, 9, 11, 9, 9, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 10, 8, 1, 11, 8, 7, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 11, 9, 1, 11, 9, 8, fence, fence, false);
			placeBlock(worldIn, fence, 10, 9, 1, structureBoundingBoxIn);

			//Torches
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.EAST), 3, 2, 3, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.WEST), 8, 2, 3, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.EAST), 3, 6, 3, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.WEST), 8, 6, 3, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.EAST), 3, 10, 3, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.WEST), 8, 10, 3, structureBoundingBoxIn);

			if(!spawns[0])
				spawns[0] = spawnConsort(5, 1, 5, structureBoundingBoxIn, worldIn, chunkGeneratorIn, EnumConsort.MerchantType.FOOD, 1);
			if(!spawns[1])
				spawns[1] = spawnConsort(6, 5, 5, structureBoundingBoxIn, worldIn, chunkGeneratorIn, EnumConsort.getRandomMerchant(randomIn), 1);
			if(!spawns[2])
				spawns[2] = spawnConsort(5, 9, 5, structureBoundingBoxIn, worldIn, chunkGeneratorIn, EnumConsort.MerchantType.GENERAL, 1);

			return true;
		}
	}
	
	public static class HighNakInn1 extends ConsortVillagePieces.ConsortVillagePiece
	{
		public HighNakInn1(ConsortVillageCenter.VillageCenter start, Random rand, MutableBoundingBox boundingBox, Direction facing)
		{
			super(MSStructurePieces.HIGH_NAK_INN, 0, 3);
			setOrientation(facing);
			this.boundingBox = boundingBox;
		}
		
		public HighNakInn1(TemplateManager templates, CompoundNBT nbt)
		{
			super(MSStructurePieces.HIGH_NAK_INN, nbt, 3);
		}
		
		public static HighNakInn1 createPiece(ConsortVillageCenter.VillageCenter start, List<StructurePiece> componentList, Random rand, int x, int y, int z, Direction facing)
		{
			MutableBoundingBox structureboundingbox = MutableBoundingBox.orientBox(x, y, z, 0, 0, 0, 12, 20, 11, facing);
			return StructurePiece.findCollisionPiece(componentList, structureboundingbox) == null ? new HighNakInn1(start, rand, structureboundingbox, facing) : null;
		}

		@Override
		public boolean postProcess(ISeedReader worldIn, StructureManager manager, ChunkGenerator chunkGeneratorIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn, BlockPos pos)
		{
			if (averageGroundLvl < 0)
			{
				averageGroundLvl = getAverageGroundLevel(worldIn, chunkGeneratorIn, structureBoundingBoxIn);

				if (averageGroundLvl < 0)
				{
					return true;
				}

				boundingBox.move(0, averageGroundLvl - boundingBox.y0 - 1, 0);
			}

			StructureBlockRegistry blocks = StructureBlockRegistry.getOrDefault(chunkGeneratorIn);
			BlockState buildBlock = blocks.getBlockState("structure_primary");
			BlockState stairs1 = blocks.getStairs("structure_primary_stairs", Direction.NORTH, false);
			BlockState stairs2 = blocks.getStairs("structure_primary_stairs", Direction.SOUTH, false);
			BlockState stairs3 = blocks.getStairs("structure_primary_stairs", Direction.WEST, false);
			BlockState stairs = blocks.getStairs("structure_secondary_stairs", Direction.SOUTH, false);
			BlockState doorBlock = blocks.getBlockState("village_door");
			BlockState floorBlock = blocks.getBlockState("structure_secondary");
			BlockState fence = blocks.getBlockState("village_fence");
			BlockState torch = blocks.getBlockState("wall_torch");
			BlockState carpet = blocks.getBlockState("carpet");

			//Floor
			generateBox(worldIn, structureBoundingBoxIn, 3, -1, 3, 8, 2, 7, floorBlock, floorBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, -1, 2, 8, 1, 2, floorBlock, floorBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 1, 1, 8, 1, 1, stairs, stairs, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 2, 2, 8, 2, 2, stairs, stairs, false);

			//Base walls and floors
			generateBox(worldIn, structureBoundingBoxIn, 2, -1, 8, 9, 19, 8, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 2, -1, 1, 2, 14, 7, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 9, -1, 1, 9, 14, 7, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 10, 1, 8, 14, 1, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 2, 15, 3, 2, 19, 7, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 9, 15, 3, 9, 19, 7, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 15, 3, 8, 19, 3, buildBlock, buildBlock, false);

			generateBox(worldIn, structureBoundingBoxIn, 3, 6, 2, 8, 6, 7, floorBlock, floorBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 10, 2, 8, 10, 7, floorBlock, floorBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 6, 1, 8, 6, 1, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 14, 2, 8, 14, 3, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 14, 4, 8, 14, 7, floorBlock, floorBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 18, 4, 8, 18, 7, buildBlock, buildBlock, false);

			//Remove blocks in front of passages
			clearFront(worldIn, structureBoundingBoxIn, 2, 10, 1, 0);

			//Floor furnishing and doors
			generateAirBox(worldIn, structureBoundingBoxIn, 3, 3, 1, 8, 5, 7);
			generateAirBox(worldIn, structureBoundingBoxIn, 3, 2, 1, 8, 2, 1);
			generateBox(worldIn, structureBoundingBoxIn, 3, 3, 4, 8, 3, 4, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 7, 5, 8, 7, 5, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 7, 1, 8, 7, 1, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 11, 4, 8, 11, 4, buildBlock, buildBlock, false);
			generateDoor(worldIn, structureBoundingBoxIn, randomIn, 9, 7, 2, Direction.WEST, doorBlock.getBlock(), DoorHingeSide.LEFT);
			generateDoor(worldIn, structureBoundingBoxIn, randomIn, 2, 7, 2, Direction.EAST, doorBlock.getBlock(), DoorHingeSide.LEFT);
			generateDoor(worldIn, structureBoundingBoxIn, randomIn, 5, 11, 8, Direction.NORTH, doorBlock.getBlock(), DoorHingeSide.LEFT);
			generateDoor(worldIn, structureBoundingBoxIn, randomIn, 6, 11, 8, Direction.NORTH, doorBlock.getBlock(), DoorHingeSide.RIGHT);
			generateDoor(worldIn, structureBoundingBoxIn, randomIn, 5, 15, 3, Direction.SOUTH, doorBlock.getBlock(), DoorHingeSide.RIGHT);
			generateDoor(worldIn, structureBoundingBoxIn, randomIn, 6, 15, 3, Direction.SOUTH, doorBlock.getBlock(), DoorHingeSide.LEFT);

			//Stairs 1
			generateAirBox(worldIn, structureBoundingBoxIn, 10, 1, 1, 11, 5, 10);
			generateAirBox(worldIn, structureBoundingBoxIn, 3, 1, 9, 9, 5, 10);

			generateBox(worldIn, structureBoundingBoxIn, 10, 0, 1, 11, 0, 10, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 3, 0, 9, 9, 0, 10, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 11, 1, 1, 11, 1, 10, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 4, 1, 10, 10, 1, 10, fence, fence, false);
			placeBlock(worldIn, stairs3, 3, 1, 9, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 3, 1, 10, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 3, 2, 10, 4, 2, 10, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 2, 1, 9, 2, 1, 10, buildBlock, buildBlock, false);
			placeBlock(worldIn, stairs3, 2, 2, 9, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 2, 2, 10, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 1, 3, 10, 3, 3, 10, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 2, 8, 1, 2, 10, buildBlock, buildBlock, false);
			placeBlock(worldIn, stairs1, 1, 3, 8, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 0, 3, 8, 0, 3, 10, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 4, 8, 0, 4, 10, fence, fence, false);
			placeBlock(worldIn, fence, 1, 4, 10, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 0, 3, 7, 1, 3, 7, buildBlock, buildBlock, false);
			placeBlock(worldIn, stairs1, 1, 4, 7, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 0, 4, 7, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 0, 5, 7, 0, 5, 8, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 4, 6, 1, 4, 6, buildBlock, buildBlock, false);
			placeBlock(worldIn, stairs1, 1, 5, 6, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 0, 5, 6, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 0, 6, 6, 0, 6, 7, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 5, 5, 1, 5, 5, buildBlock, buildBlock, false);
			placeBlock(worldIn, stairs1, 1, 6, 5, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 0, 6, 5, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 0, 7, 1, 0, 7, 6, fence, fence, false);
			placeBlock(worldIn, fence, 1, 7, 1, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 0, 6, 1, 1, 6, 4, buildBlock, buildBlock, false);

			//Stairs 2
			generateBox(worldIn, structureBoundingBoxIn, 10, 6, 1, 11, 6, 3, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 11, 7, 1, 11, 7, 2, fence, fence, false);
			placeBlock(worldIn, fence, 10, 7, 1, structureBoundingBoxIn);
			placeBlock(worldIn, stairs2, 10, 7, 3, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 11, 7, 3, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 11, 8, 2, 11, 8, 3, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 10, 7, 4, 11, 7, 4, buildBlock, buildBlock, false);
			placeBlock(worldIn, stairs2, 10, 8, 4, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 11, 8, 4, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 11, 9, 3, 11, 9, 4, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 10, 8, 5, 11, 8, 5, buildBlock, buildBlock, false);
			placeBlock(worldIn, stairs2, 10, 9, 5, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 11, 9, 5, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 11, 10, 4, 11, 10, 5, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 10, 9, 6, 11, 9, 6, buildBlock, buildBlock, false);
			placeBlock(worldIn, stairs2, 10, 10, 6, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 11, 10, 6, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 10, 10, 7, 11, 10, 10, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 11, 11, 5, 11, 11, 10, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 10, 9, 9, 10, 10, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 11, 10, 10, 11, 10, fence, fence, false);

			//Stairs 3
			generateBox(worldIn, structureBoundingBoxIn, 0, 10, 7, 1, 10, 8, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 11, 8, 0, 11, 9, fence, fence, false);
			placeBlock(worldIn, stairs1, 1, 11, 7, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 0, 11, 7, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 0, 12, 7, 0, 12, 8, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 11, 6, 1, 11, 6, buildBlock, buildBlock, false);
			placeBlock(worldIn, stairs1, 1, 12, 6, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 0, 12, 6, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 0, 13, 6, 0, 13, 7, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 12, 5, 1, 12, 5, buildBlock, buildBlock, false);
			placeBlock(worldIn, stairs1, 1, 13, 5, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 0, 13, 5, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 0, 14, 5, 0, 14, 6, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 13, 4, 1, 13, 4, buildBlock, buildBlock, false);
			placeBlock(worldIn, stairs1, 1, 14, 4, structureBoundingBoxIn);
			placeBlock(worldIn, buildBlock, 0, 14, 4, structureBoundingBoxIn);
			generateBox(worldIn, structureBoundingBoxIn, 0, 15, 1, 0, 15, 5, fence, fence, false);
			generateBox(worldIn, structureBoundingBoxIn, 0, 14, 1, 1, 14, 3, buildBlock, buildBlock, false);
			generateBox(worldIn, structureBoundingBoxIn, 1, 15, 1, 9, 15, 1, fence, fence, false);
			placeBlock(worldIn, fence, 9, 15, 2, structureBoundingBoxIn);

			//Inn decoration
			generateBox(worldIn, structureBoundingBoxIn, 3, 15, 4, 7, 15, 7, carpet, carpet, false);
			placeBlock(worldIn, Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.WEST), 8, 15, 7, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.CRAFTING_TABLE.defaultBlockState(), 8, 15, 6, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.WEST), 8, 15, 5, structureBoundingBoxIn);
			placeBlock(worldIn, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.WEST), 8, 15, 4, structureBoundingBoxIn);
			generateBed(worldIn, structureBoundingBoxIn, randomIn, 3, 15, 6, Direction.SOUTH, Blocks.RED_BED.defaultBlockState());

			//Torches
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.EAST), 3, 4, 2, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.WEST), 8, 4, 2, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.EAST), 3, 8, 3, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.WEST), 8, 8, 3, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.NORTH), 3, 13, 7, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.NORTH), 8, 13, 7, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.SOUTH), 3, 13, 2, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.SOUTH), 8, 13, 2, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.NORTH), 3, 17, 7, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.NORTH), 8, 17, 7, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.SOUTH), 3, 17, 4, structureBoundingBoxIn);
			placeBlock(worldIn, torch.setValue(WallTorchBlock.FACING, Direction.SOUTH), 8, 17, 4, structureBoundingBoxIn);

			if(!spawns[0])
				spawns[0] = spawnConsort(5, 3, 5, structureBoundingBoxIn, worldIn, chunkGeneratorIn, EnumConsort.MerchantType.FOOD, 1);
			if(!spawns[1])
				spawns[1] = spawnConsort(6, 7, 6, structureBoundingBoxIn, worldIn, chunkGeneratorIn, EnumConsort.getRandomMerchant(randomIn), 1);
			if(!spawns[2])
				spawns[2] = spawnConsort(5, 11, 3, structureBoundingBoxIn, worldIn, chunkGeneratorIn, EnumConsort.MerchantType.GENERAL, 1);

			return true;
		}
	}
}