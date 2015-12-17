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

import hudson.EnvVars;
import hudson.model.*;
import jenkins.model.Jenkins;

import java.io.IOException;

public class GlobalVariablesFreestyleBuildAdapter extends FreeStyleBuild {

    public GlobalVariablesFreestyleBuildAdapter() throws IOException {
        super(new FreeStyleProject((ItemGroup) Jenkins.getInstance(), "globalVariablesAdapter"));
    }

    @Override
    public EnvVars getEnvironment(TaskListener log) throws IOException, InterruptedException {
        return Computer.currentComputer().getEnvironment();
    }
}
