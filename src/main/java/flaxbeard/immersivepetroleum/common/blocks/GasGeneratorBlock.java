package flaxbeard.immersivepetroleum.common.blocks;

import java.util.Collections;
import java.util.List;

import flaxbeard.immersivepetroleum.common.blocks.metal.GasGeneratorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

public class GasGeneratorBlock extends IPBlockBase{
	private static final Material material=new Material(MaterialColor.IRON, false, true, false, false, false, false, false, PushReaction.BLOCK);
	
	public static final DirectionProperty FACING=DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
	
	public GasGeneratorBlock(){
		super("gas_generator", Block.Properties.create(material).hardnessAndResistance(3.0F, 8.0F));
		
		setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder){
		builder.add(FACING);
	}
	
	@Override
	public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer){
		return layer==BlockRenderLayer.TRANSLUCENT || layer==BlockRenderLayer.SOLID;
	}
	
	@Override
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.TRANSLUCENT;
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit){
		ItemStack held=player.getHeldItem(handIn);
		if(held!=ItemStack.EMPTY){
			GasGeneratorTileEntity te=(GasGeneratorTileEntity)worldIn.getTileEntity(pos);
			if(te!=null){
				return te.interact(hit.getFace(), player, handIn, held, (float)hit.getHitVec().x, (float)hit.getHitVec().y, (float)hit.getHitVec().z);
			}
		}
		return true;
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		if(!worldIn.isRemote){
			TileEntity te=worldIn.getTileEntity(pos);
			if(te instanceof GasGeneratorTileEntity){
				((GasGeneratorTileEntity)te).readOnPlacement(placer, stack);
			}
		}
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder){
		ServerWorld world=builder.getWorld();
		BlockPos pos=builder.get(LootParameters.POSITION);
		
		TileEntity te=world.getTileEntity(pos);
		if(te instanceof GasGeneratorTileEntity){
			return ((GasGeneratorTileEntity)te).getTileDrops(null);
		}
		
		return Collections.emptyList();
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos){
		return true;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state){
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world){
		GasGeneratorTileEntity te=new GasGeneratorTileEntity();
		te.setFacing(state.get(FACING));
		return te;
	}
}
