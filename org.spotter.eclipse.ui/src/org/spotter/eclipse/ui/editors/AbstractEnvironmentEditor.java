/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.spotter.eclipse.ui.editors;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.lpe.common.config.ConfigParameterDescription;
import org.spotter.eclipse.ui.Activator;
import org.spotter.eclipse.ui.ServiceClientWrapper;
import org.spotter.eclipse.ui.UICoreException;
import org.spotter.eclipse.ui.model.ExtensionMetaobject;
import org.spotter.eclipse.ui.model.ExtensionItem;
import org.spotter.eclipse.ui.model.xml.EnvironmentModelWrapper;
import org.spotter.eclipse.ui.model.xml.IModelWrapper;
import org.spotter.eclipse.ui.model.xml.MeasurementEnvironmentFactory;
import org.spotter.eclipse.ui.util.SpotterProjectSupport;
import org.spotter.shared.configuration.SpotterExtensionType;
import org.spotter.shared.environment.model.XMeasurementEnvObject;
import org.spotter.shared.environment.model.XMeasurementEnvironment;

/**
 * Abstract base class for environment editors.
 * 
 * @author Denis Knoepfle
 * 
 */
public abstract class AbstractEnvironmentEditor extends AbstractExtensionsEditor {

	/**
	 * Implementing editors must return the corresponding environment objects.
	 * 
	 * @return the corresponding environment objects
	 */
	protected abstract List<XMeasurementEnvObject> getMeasurementEnvironmentObjects();

	/**
	 * Implementing editors must return the corresponding extension type.
	 * 
	 * @return the corresponding extension type
	 */
	protected abstract SpotterExtensionType getExtensionType();

	@Override
	public ExtensionItem getInitialExtensionsInput() {
		List<XMeasurementEnvObject> envObjects = getMeasurementEnvironmentObjects();
		ExtensionItem input = new ExtensionItem();

		String projectName = getProject().getName();
		ServiceClientWrapper client = Activator.getDefault().getClient(projectName);
		for (XMeasurementEnvObject envObj : envObjects) {
			String extName = envObj.getExtensionName();
			Set<ConfigParameterDescription> configParams = client.getExtensionConfigParamters(extName);

			if (configParams == null) {
				MessageDialog.openWarning(null, TITLE_ERR_DIALOG,
						"Creating initial input failed. Cause: Missing information to fully initialize ExtensionItem");
				return new ExtensionItem();
			}

			ExtensionMetaobject extension = new ExtensionMetaobject(projectName, extName, configParams);
			IModelWrapper wrapper = new EnvironmentModelWrapper(extension, envObjects, envObj);
			input.addItem(new ExtensionItem(wrapper));
		}
		return input;
	}

	@Override
	public ExtensionMetaobject[] getAvailableExtensions() {
		String projectName = getProject().getName();
		ServiceClientWrapper client = Activator.getDefault().getClient(projectName);
		return client.getAvailableExtensions(getExtensionType());
	}

	@Override
	public IModelWrapper createModelWrapper(Object parent, ExtensionMetaobject extensionComponent) {
		List<XMeasurementEnvObject> envObjects = getMeasurementEnvironmentObjects();
		XMeasurementEnvObject envObject = new XMeasurementEnvObject();
		envObject.setExtensionName(extensionComponent.getExtensionName());
		envObjects.add(envObject);
		return new EnvironmentModelWrapper(extensionComponent, envObjects, envObject);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		AbstractSpotterEditorInput input = (AbstractSpotterEditorInput) getEditorInput();

		try {
			MeasurementEnvironmentFactory factory = MeasurementEnvironmentFactory.getInstance();
			XMeasurementEnvironment measurementEnv = factory.parseXMLFile(input.getPath().toString());

			applyChanges(measurementEnv);
			SpotterProjectSupport.saveEnvironment(input.getFile(), measurementEnv);

			super.doSave(monitor);
		} catch (Exception e) {
			MessageDialog.openError(null, TITLE_ERR_DIALOG, ERR_MSG_SAVE + e.getMessage());
		}
	}

	@Override
	protected boolean isInputApplicable(AbstractSpotterEditorInput input) throws Exception {
		MeasurementEnvironmentFactory factory = MeasurementEnvironmentFactory.getInstance();
		XMeasurementEnvironment env = factory.parseXMLFile(input.getPath().toString());

		return env != null;

		/*
		 * if (env == null || extensionsMap == null && loadExtensions() == null)
		 * { return false; }
		 * 
		 * // TODO: currently only checks the keys but not the values for
		 * (XMeasurementEnvObject envObj : getMeasurementEnvironmentObjects()) {
		 * String extensionName = envObj.getExtensionName(); if
		 * (!extensionsMap.containsKey(extensionName)) {
		 * System.err.println("key '" + extensionName + "' not supported");
		 * return false; } if (envObj.getConfig() != null) {
		 * ExtensionDescription extension =
		 * extensionsMap.get(envObj.getExtensionName()); for (XMConfiguration
		 * xmConfig : envObj.getConfig()) { boolean foundConfigKey = false; for
		 * (ConfigParameterDescription desc : extension.getConfigParams()) { if
		 * (xmConfig.getKey().equals(desc.getName())) { foundConfigKey = true;
		 * break; } } if (!foundConfigKey) { return false; } } } }
		 * 
		 * return true;
		 */
	}

	@Override
	protected void makeInputApplicable(AbstractSpotterEditorInput input) throws UICoreException {
		XMeasurementEnvironment mEnv = MeasurementEnvironmentFactory.getInstance().createMeasurementEnvironment();
		SpotterProjectSupport.saveEnvironment(input.getFile(), mEnv);
	}

}
