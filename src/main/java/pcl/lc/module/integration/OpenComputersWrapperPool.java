package pcl.lc.module.integration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.Block;
import li.cil.oc.api.driver.MethodWhitelist;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumStargateState;
import pcl.lc.api.INaquadahGeneratorAccess;
import pcl.lc.api.IStargateAccess;
import pcl.lc.api.IStargateControllerAccess;
import pcl.lc.core.AddressingError;
import pcl.lc.core.AddressingError.CoordRangeError;
import pcl.lc.core.AddressingError.DimensionRangeError;
import pcl.lc.core.GateAddressHelper;
import pcl.lc.tileentity.TileEntityStargateBase;

public class OpenComputersWrapperPool {

	public static class OpenComputersDriver implements Block {
		@Override
		public boolean worksWith(World world, int x, int y, int z) {
			int id = world.getBlockId(x, y, z);
			return (id == LanteaCraft.Blocks.stargateBaseBlock.blockID)
					|| (id == LanteaCraft.Blocks.naquadahGenerator.blockID)
					|| (id == LanteaCraft.Blocks.stargateControllerBlock.blockID);
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			int id = world.getBlockId(x, y, z);
			try {
				if (id == LanteaCraft.Blocks.stargateBaseBlock.blockID) {
					IStargateAccess base = (IStargateAccess) world.getBlockTileEntity(x, y, z);
					return new OpenComputersWrapperPool.StargateAccessWrapper(base);
				} else if (id == LanteaCraft.Blocks.naquadahGenerator.blockID) {
					INaquadahGeneratorAccess generator = (INaquadahGeneratorAccess) world.getBlockTileEntity(x, y, z);
					return new OpenComputersWrapperPool.NaquadahGeneratorAccessWrapper(generator);
				} else if (id == LanteaCraft.Blocks.stargateControllerBlock.blockID) {
					IStargateControllerAccess dhd = (IStargateControllerAccess) world.getBlockTileEntity(x, y, z);
					return new OpenComputersWrapperPool.StargateControllerAccessWrapper(dhd);
				} else
					throw new RuntimeException("Driver.Block handler specified invalid typeof!");
			} catch (Throwable t) {
				LanteaCraft.getLogger().log(Level.WARNING,
						"Failed when handling OpenComputers createEnvironment request.", t);
				return null;
			}
		}
	}

	private interface IHookManagedEnvironment extends ManagedEnvironment {
		public String getComponentName();
	}

	private abstract static class OpenComputersHostStub implements IHookManagedEnvironment, MethodWhitelist {

		private String[] methodList = null;
		protected Node node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).create();

		@Override
		public Node node() {
			return node;
		}

		@Override
		public void load(final NBTTagCompound nbt) {
			if (node != null)
				node.load(nbt.getCompoundTag("node"));
		}

		@Override
		public void save(final NBTTagCompound nbt) {
			if (node != null) {
				NBTTagCompound nodeTag = new NBTTagCompound();
				node.save(nodeTag);
				nbt.setCompoundTag("node", nodeTag);
			}
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void onConnect(Node node) {
		}

		@Override
		public void onDisconnect(Node node) {
		}

		@Override
		public void onMessage(Message message) {
		}

		/**
		 * Method to get all methods with Callback annotation in the provided
		 * class.
		 * 
		 * @param clazz
		 *            Our local class.
		 * @return List of all Callback methods (cached).
		 */
		protected String[] getMethods(Class<? extends OpenComputersHostStub> clazz) {
			if (methodList != null)
				return methodList;
			ArrayList<String> methodView = new ArrayList<String>();
			for (Method method : clazz.getMethods()) {
				Annotation[] annotations = method.getAnnotations();
				if (annotations != null && annotations.length > 0) {
					for (Annotation annotation : annotations)
						if (annotation.annotationType().equals(Callback.class))
							methodView.add(method.getName());
				}
			}
			methodList = methodView.toArray(new String[0]);
			return methodList;
		}
	}

	public static class StargateAccessWrapper extends OpenComputersHostStub {

		private final IStargateAccess access;
		private EnumStargateState stateWatcher;

		public StargateAccessWrapper(IStargateAccess access) {
			this.access = access;
		}

		@Override
		public String getComponentName() {
			return "stargate";
		}

		@Override
		public void update() {
			if (access.getState() != stateWatcher) {
				stateWatcher = access.getState();
				switch (stateWatcher) {
				case Idle:
					node.sendToReachable("computer.signal", "sgIdle", true);
					break;
				case Dialling:
					if (access.isOutgoingConnection())
						node.sendToReachable("computer.signal", "sgOutgoing", access.getConnectionAddress());
					else
						node.sendToReachable("computer.signal", "sgIncoming", Character.toString(access.getConnectionAddress().charAt(access.getEncodedChevrons())));
					break;
				case InterDialling:
					node.sendToReachable("computer.signal", "sgChevronEncode", access.getEncodedChevrons());
					break;
				case Transient:
					node.sendToReachable("computer.signal", "sgWormholeOpening", true);
					break;
				case Disconnecting:
					node.sendToReachable("computer.signal", "sgWormholeClosing", true );
					break;
				case Connected:
					node.sendToReachable("computer.signal", "sgWormholeStable", true);
					break;
				default:
					break;
				}
			}
		}

		@Callback
		public Object[] greet(Context context, Arguments args) {
			return new Object[] { String.format("Hello, %s!", args.checkString(0)) };
		}

		@Callback
		public Object[] getInterfaceVersion(Context context, Arguments args) throws Exception {
			return new Object[] { BuildInfo.versionNumber + "." + BuildInfo.getBuildNumber() };
		}
		
		@Callback
		public Object[] dial(Context context, Arguments args) throws Exception {
			return dialTheGate(args.checkString(0).toUpperCase());
		}

		@Callback
		public Object[] connect(Context context, Arguments args) throws Exception {
			return dialTheGate(args.checkString(0).toUpperCase());
		}

		public Object[] dialTheGate(String address) throws Exception {
			if (address.length() != 7 && address.length() != 9)
				throw new Exception("Stargate addresses must be 7 or 9 characters.");
			else if (address.equals(access.getLocalAddress())
					|| address.equals(access.getLocalAddress().substring(0, 7)))
				throw new Exception("Stargate cannot connect to itself.");
			else if (!access.connect(address))
				throw new Exception("Stargate cannot dial now.");
			return new Object[] { true };
		}
		
		@Callback
		public Object[] disconnect(Context context, Arguments args) throws Exception {
			if (!access.isBusy())
				throw new Exception("Stargate is not connected");
			if (!access.disconnect())
				throw new Exception("Stargate cannot be disconnected");
			return new Object[] { true };
		}

		@Callback
		public Object[] isConnected(Context context, Arguments args) {
			return new Object[] { access.isBusy() };
		}

		@Callback
		public Object[] getAddress(Context context, Arguments args) {
			return new Object[] { access.getLocalAddress() };
		}

		@Callback
		public Object[] isDialing(Context context, Arguments args) {
			return new Object[] { (access.getState() == EnumStargateState.Dialling
					|| access.getState() == EnumStargateState.Dialling || access.getState() == EnumStargateState.InterDialling) };
		}

		@Callback
		public Object[] isComplete(Context context, Arguments args) {
			return new Object[] { access.isValid() };
		}

		@Callback
		public Object[] isBusy(Context context, Arguments args) throws Exception {
			String address = args.checkString(0).toUpperCase();
			if (address.length() != 7 && address.length() != 9)
				throw new Exception("Stargate addresses must be 7 or 9 characters.");
			else if (address.equals(access.getLocalAddress())
					|| address.equals(access.getLocalAddress().substring(0, 7)))
				throw new Exception("Stargate cannot connect to itself.");
			try {
				TileEntityStargateBase dte = GateAddressHelper.findStargate(access.getLocation(), address);
				if ((EnumStargateState) dte.getAsStructure().getMetadata("state") != EnumStargateState.Idle)
					return new Object[] { true };
			} catch (Throwable thrown) {
				if (thrown instanceof CoordRangeError || thrown instanceof DimensionRangeError)
					throw new Exception(thrown.getMessage());
				else if (thrown instanceof AddressingError)
					throw new Exception("Addressing error: " + thrown.getMessage());
			}
			return new Object[] { false };
		}

		@Callback
		public Object[] hasFuel(Context context, Arguments args) {
			return new Object[] { access.getRemainingConnectionTime() > 0 && access.getRemainingDials() > 0 };
		}

		@Callback
		public Object[] isValidAddress(Context context, Arguments args) throws Exception {
			String address = args.checkString(0).toUpperCase();
			if (address.length() != 7 && address.length() != 9)
				throw new Exception("Stargate addresses must be 7 or 9 characters.");
			else if (address.equals(access.getLocalAddress())
					|| address.equals(access.getLocalAddress().substring(0, 7)))
				throw new Exception("Stargate cannot connect to itself.");
			try {
				if (address.equals(access.getLocalAddress()))
					throw new Exception("Stargate cannot connect to itself");
				if (GateAddressHelper.findStargate(access.getLocation(), address) == null)
					return new Object[] { false };
			} catch (Throwable thrown) {
				if (thrown instanceof CoordRangeError || thrown instanceof DimensionRangeError)
					throw new Exception(thrown.getMessage());
				else if (thrown instanceof AddressingError)
					throw new Exception("Addressing error: " + thrown.getMessage());
			}
			return new Object[] { true };
		}

		@Override
		public String[] whitelistedMethods() {
			return getMethods(this.getClass());
		}
	}

	public static class StargateControllerAccessWrapper extends OpenComputersHostStub {
		private final IStargateControllerAccess access;

		public StargateControllerAccessWrapper(IStargateControllerAccess access) {
			super();
			this.access = access;
		}

		@Override
		public void update() {
		}

		@Override
		public String getComponentName() {
			return "stargate_controller";
		}

		@Callback
		public Object[] greet(Context context, Arguments args) {
			return new Object[] { String.format("Hello, %s!", args.checkString(0)) };
		}

		@Callback
		public Object[] isValid(Context context, Arguments args) {
			return new Object[] { access.isValid() };
		}

		@Callback
		public Object[] isBusy(Context context, Arguments args) {
			return new Object[] { access.isBusy() };
		}

		@Callback
		public Object[] ownsCurrentConnection(Context context, Arguments args) {
			return new Object[] { access.ownsCurrentConnection() };
		}

		@Callback
		public Object[] getDialledAddress(Context context, Arguments args) {
			return new Object[] { access.getDialledAddress() };
		}

		@Callback
		public Object[] disconnect(Context context, Arguments args) throws Exception {
			if (!access.isBusy())
				throw new Exception("Stargate is not connected");
			if (!access.disconnect())
				throw new Exception("Stargate cannot be disconnected");
			return new Object[] { true };
		}

		@Override
		public String[] whitelistedMethods() {
			return getMethods(this.getClass());
		}
	}

	public static class NaquadahGeneratorAccessWrapper extends OpenComputersHostStub {

		private final INaquadahGeneratorAccess access;

		public NaquadahGeneratorAccessWrapper(INaquadahGeneratorAccess access) {
			this.access = access;
		}

		@Override
		public void update() {
		}

		@Override
		public String getComponentName() {
			return "naquadah_generator";
		}

		@Callback
		public Object[] greet(Context context, Arguments args) {
			return new Object[] { String.format("Hello, %s!", args.checkString(0)) };
		}

		@Callback
		public Object[] isEnabled(Context context, Arguments args) {
			return new Object[] { access.isEnabled() };
		}

		@Callback
		public Object[] setEnabled(Context context, Arguments args) throws Exception {
			if (!(args.checkBoolean(0)))
				throw new Exception("boolean expected");
			boolean state = args.checkBoolean(0);
			if (state != access.setEnabled(state))
				throw new Exception("Cannot set Naquadah Generator state");
			return new Object[] { access.isEnabled() };
		}

		@Callback
		public Object[] getStoredEnergy(Context context, Arguments args) {
			return new Object[] { access.getStoredEnergy() };
		}

		@Callback
		public Object[] getMaximumStoredEnergy(Context context, Arguments args) {
			return new Object[] { access.getMaximumStoredEnergy() };
		}

		@Override
		public String[] whitelistedMethods() {
			return getMethods(this.getClass());
		}
	}

}
