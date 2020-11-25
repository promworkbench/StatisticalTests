package org.processmining.statisticaltests.test;

import java.math.BigInteger;
import java.util.Collection;
import java.util.concurrent.Executor;

import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginContextID;
import org.processmining.framework.plugin.PluginDescriptor;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.PluginManager;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.plugin.ProMFuture;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.RecursiveCallException;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.framework.plugin.events.PluginLifeCycleEventListener.List;
import org.processmining.framework.plugin.events.ProgressEventListener.ListenerList;
import org.processmining.framework.plugin.impl.FieldSetException;
import org.processmining.framework.providedobjects.ProvidedObjectManager;
import org.processmining.framework.util.Pair;

public class FakeContext implements PluginContext {

	private Progress progress = new Progress() {

		public void setValue(int value) {
			// TODO Auto-generated method stub

		}

		public void setMinimum(int value) {
			// TODO Auto-generated method stub

		}

		public void setMaximum(int value) {
			// TODO Auto-generated method stub

		}

		public void setIndeterminate(boolean makeIndeterminate) {
			// TODO Auto-generated method stub

		}

		public void setCaption(String message) {
			// TODO Auto-generated method stub

		}

		public boolean isIndeterminate() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isCancelled() {
			// TODO Auto-generated method stub
			return false;
		}

		public void inc() {
			// TODO Auto-generated method stub

		}

		public int getValue() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getMinimum() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getMaximum() {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getCaption() {
			// TODO Auto-generated method stub
			return null;
		}

		public void cancel() {
			// TODO Auto-generated method stub

		}
	};
	private ProMFuture<?> future = new ProMFuture<Object>(BigInteger.class, "") {

		protected Object doInBackground() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

	};

	public PluginManager getPluginManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public ProvidedObjectManager getProvidedObjectManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public ConnectionManager getConnectionManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public PluginContextID createNewPluginContextID() {
		// TODO Auto-generated method stub
		return null;
	}

	public void invokePlugin(PluginDescriptor plugin, int index, Object... objects) {
		// TODO Auto-generated method stub

	}

	public void invokeBinding(PluginParameterBinding binding, Object... objects) {
		// TODO Auto-generated method stub

	}

	public Class<? extends PluginContext> getPluginContextType() {
		// TODO Auto-generated method stub
		return null;
	}

	public <T, C extends Connection> Collection<T> tryToFindOrConstructAllObjects(Class<T> type,
			Class<C> connectionType, String role, Object... input) throws ConnectionCannotBeObtained {
		// TODO Auto-generated method stub
		return null;
	}

	public <T, C extends Connection> T tryToFindOrConstructFirstObject(Class<T> type, Class<C> connectionType,
			String role, Object... input) throws ConnectionCannotBeObtained {
		// TODO Auto-generated method stub
		return null;
	}

	public <T, C extends Connection> T tryToFindOrConstructFirstNamedObject(Class<T> type, String name,
			Class<C> connectionType, String role, Object... input) throws ConnectionCannotBeObtained {
		// TODO Auto-generated method stub
		return null;
	}

	public PluginContext createChildContext(String label) {
		// TODO Auto-generated method stub
		return null;
	}

	public Progress getProgress() {
		return progress;
	}

	public ListenerList getProgressEventListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	public List getPluginLifeCycleEventListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	public PluginContextID getID() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public Pair<PluginDescriptor, Integer> getPluginDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public PluginContext getParentContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public java.util.List<PluginContext> getChildContexts() {
		// TODO Auto-generated method stub
		return null;
	}

	public PluginExecutionResult getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public ProMFuture<?> getFutureResult(int i) {
		return future;
	}

	public Executor getExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDistantChildOf(PluginContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setFuture(PluginExecutionResult resultToBe) {
		// TODO Auto-generated method stub

	}

	public void setPluginDescriptor(PluginDescriptor descriptor, int methodIndex)
			throws FieldSetException, RecursiveCallException {
		// TODO Auto-generated method stub

	}

	public boolean hasPluginDescriptorInPath(PluginDescriptor descriptor, int methodIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	public void log(String message, MessageLevel level) {
		// TODO Auto-generated method stub

	}

	public void log(String message) {
		// TODO Auto-generated method stub

	}

	public void log(Throwable exception) {
		// TODO Auto-generated method stub

	}

	public org.processmining.framework.plugin.events.Logger.ListenerList getLoggingListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	public PluginContext getRootContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean deleteChild(PluginContext child) {
		// TODO Auto-generated method stub
		return false;
	}

	public <T extends Connection> T addConnection(T c) {
		// TODO Auto-generated method stub
		return null;
	}

	public void clear() {
		// TODO Auto-generated method stub

	}

}
