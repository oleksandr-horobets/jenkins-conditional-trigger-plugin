/*
 * Copyright 2015 Oleksandr Horobets.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jenkinsci.plugins.conditionaltrigger;

import antlr.ANTLRException;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.StreamBuildListener;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import jenkins.model.Jenkins;
import org.jenkins_ci.plugins.run_condition.RunCondition;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("unused") //used as extension
public class ConditionalTrigger extends Trigger<AbstractProject> {

    private static final Logger LOG = Logger.getLogger(ConditionalTrigger.class.getName());

    private RunCondition runCondition;
    private Trigger targetTrigger;

    private transient GlobalVariablesFreestyleBuildAdapter globalVariablesFreestyleBuildAdapter;

    @DataBoundConstructor
    public ConditionalTrigger(RunCondition runCondition, Trigger targetTrigger) throws ANTLRException, IOException {
        super(targetTrigger != null ? targetTrigger.getSpec() : null);

        this.runCondition = runCondition;
        this.targetTrigger = targetTrigger;

        this.globalVariablesFreestyleBuildAdapter = new GlobalVariablesFreestyleBuildAdapter();
    }

    @Override
    public void run(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        StreamBuildListener buildListener = new StreamBuildListener(byteArrayOutputStream);

        try {
            if(runCondition.runPerform(getGlobalVariablesFreestyleBuildAdapter(), buildListener)){
                targetTrigger.run();
            }
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }
    }

    @Override
    public void start(AbstractProject project, boolean newInstance) {
        targetTrigger.start(project, newInstance);
    }

    private GlobalVariablesFreestyleBuildAdapter getGlobalVariablesFreestyleBuildAdapter() throws IOException {
        if(globalVariablesFreestyleBuildAdapter == null){
            globalVariablesFreestyleBuildAdapter = new GlobalVariablesFreestyleBuildAdapter();
        }

        return globalVariablesFreestyleBuildAdapter;
    }

    @SuppressWarnings("unused") //used in view
    public RunCondition getRunCondition() {
        return runCondition;
    }

    @SuppressWarnings("unused") //used in view
    public Trigger getTargetTrigger() {
        return targetTrigger;
    }

    @Extension
    public static final class DescriptorImpl extends TriggerDescriptor {
        @Override
        public boolean isApplicable(Item item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Conditional trigger";
        }

        public List<TriggerDescriptor> getTriggerDescriptors(){
            return Jenkins.getInstance().getExtensionList(TriggerDescriptor.class);
        }

        public List<? extends Descriptor<? extends RunCondition>> getConditionDescriptors(){
            return RunCondition.all();
        }
    }
}
