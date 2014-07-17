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
package org.spotter.jmeter.workload;

import org.lpe.common.config.ConfigParameterDescription;
import org.lpe.common.util.LpeSupportedTypes;
import org.spotter.core.workload.AbstractWorkloadExtension;
import org.spotter.core.workload.IWorkloadAdapter;
import org.spotter.jmeter.JMeterConfigKeys;

/**
 * Loadrunner workload extension.
 * 
 * @author Alexander Wert
 * 
 */
public class JMeterWorkloadExtension extends AbstractWorkloadExtension {

	@Override
	public String getName() {
		return "workload.adapter.jmeter";
	}

	public static final String THINK_TIME_MIN = "org.spotter.workload.jmeter.thinkTimeMin";
	public static final String THINK_TIME_MAX = "org.spotter.workload.jmeter.thinkTimeMax";

	private ConfigParameterDescription createJMeterHomeParameter() {
		ConfigParameterDescription jMeterHomeParameter = new ConfigParameterDescription(
				JMeterConfigKeys.JMETER_HOME, LpeSupportedTypes.String);
		jMeterHomeParameter.setDirectory(true);
		jMeterHomeParameter.setMandatory(true);
		jMeterHomeParameter.setDefaultValue("");
		jMeterHomeParameter
				.setDescription("The path to the JMeter root directory.");

		return jMeterHomeParameter;
	}

	private ConfigParameterDescription createJMeterScenarioFileParameter() {
		ConfigParameterDescription jMeterScenarioFileParameter = new ConfigParameterDescription(
				JMeterConfigKeys.SCENARIO_FILE, LpeSupportedTypes.String);
		jMeterScenarioFileParameter.setFile(true);
		jMeterScenarioFileParameter.setMandatory(true);
		jMeterScenarioFileParameter.setDefaultValue("");
		jMeterScenarioFileParameter
				.setDescription("The path to the JMeter user script file.");

		return jMeterScenarioFileParameter;
	}

	private ConfigParameterDescription createJMeterResultFileParameter() {
		ConfigParameterDescription jMeterResultFileParameter = new ConfigParameterDescription(
				JMeterConfigKeys.RESULT_FILE, LpeSupportedTypes.String);
		jMeterResultFileParameter.setFile(true);
		jMeterResultFileParameter.setMandatory(true);
		jMeterResultFileParameter.setDefaultValue("");
		jMeterResultFileParameter
				.setDescription("The path to the JMeter result CSV file!");

		return jMeterResultFileParameter;
	}

	private ConfigParameterDescription createJMeterThinkTimeMinParameter() {
		ConfigParameterDescription jMeterThinkTimeMinParameter = new ConfigParameterDescription(
				JMeterConfigKeys.THINK_TIME_MIN, LpeSupportedTypes.Integer);
		jMeterThinkTimeMinParameter.setMandatory(true);
		jMeterThinkTimeMinParameter.setDefaultValue(String.valueOf(1000));
		jMeterThinkTimeMinParameter
				.setDescription("Minimal thinktime in milli seconds!");

		return jMeterThinkTimeMinParameter;
	}

	private ConfigParameterDescription createJMeterThinkTimeMaxParameter() {
		ConfigParameterDescription jMeterThinkTimeMaxParameter = new ConfigParameterDescription(
				JMeterConfigKeys.THINK_TIME_MAX, LpeSupportedTypes.Integer);
		jMeterThinkTimeMaxParameter.setMandatory(true);
		jMeterThinkTimeMaxParameter.setDefaultValue(String.valueOf(2000));
		jMeterThinkTimeMaxParameter
				.setDescription("Maximum thinktime in milli seconds!");

		return jMeterThinkTimeMaxParameter;
	}

	@Override
	protected void initializeConfigurationParameters() {
		addConfigParameter(createJMeterHomeParameter());
		addConfigParameter(createJMeterScenarioFileParameter());
		addConfigParameter(createJMeterResultFileParameter());
		addConfigParameter(createJMeterThinkTimeMinParameter());
		addConfigParameter(createJMeterThinkTimeMaxParameter());
	}

	@Override
	public IWorkloadAdapter createExtensionArtifact() {
		return new JMeterWorkloadClient(this);
	}

	@Override
	public boolean testConnection(String host, String port) {
		return true;
	}

	@Override
	public boolean isRemoteExtension() {
		return false;
	}

}