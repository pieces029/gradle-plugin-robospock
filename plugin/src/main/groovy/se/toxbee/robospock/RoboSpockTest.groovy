/*
 * Copyright 2014 toxbee.se
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.toxbee.robospock

import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.testing.Test

/**
 * {@link RoboSpockTest} is the test task.
 *
 * @author Mazdak Farrokhzad <twingoow@gmail.com>
 * @version 1.0
 * @since Oct , 02, 2014
 */
class RoboSpockTest extends Test {
	/*
	 * Properties:
	 */

	RoboSpockConfiguration config

	/*
	 * Actions:
	 */

	@TaskAction
	public void executeTests() {
		def p = config.project

		// Make check depend on this task.
		p.tasks.getByName( JavaBasePlugin.CHECK_TASK_NAME ).dependsOn( this )

		/*
		 * Naming:
		 */

		setDescription  'Runs the unit tests using RoboSpock.'
		setGroup        JavaBasePlugin.VERIFICATION_GROUP

		/*
		 * Setup for Roboelectric:
		 */

		// set a system property for the test JVM(s)
		systemProperty 'ro.build.date.utc', '1'
		systemProperty 'ro.kernel.qemu', '0'

		def android = config.android

		systemProperty 'android.resources', android.file( "build/intermediates/res/${config.buildType}" )
		systemProperty 'android.assets', android.file( "build/intermediates/res/${config.buildType}/raw" )
		systemProperty 'android.manifest', android.file( "build/intermediates/manifests/full/${config.buildType}/AndroidManifest.xml" )
		def wd = android.file( 'src/main' )
		if ( wd.exists() ) {
			workingDir = wd
		}

		/*
		 * Test JVM settings:
		 */

		// set heap size for the test JVM(s)
		minHeapSize = "128m"
		maxHeapSize = "1024m"

		// set JVM arguments for the test JVM(s)
		jvmArgs '-XX:MaxPermSize=512m'

		/*
		 * Logging:
		 */

		// listen to events in the test execution lifecycle
		beforeTest { descriptor ->
			logger.lifecycle( "Running test: " + descriptor.toString() )
		}

		testLogging {
			// set options for log level LIFECYCLE
			events "failed"
			exceptionFormat "short"
			// set options for log level DEBUG
			debug {
				events "started", "skipped", "failed"
				exceptionFormat "full"
			}

			// remove standard output/error logging from --info builds
			// by assigning only 'failed' and 'skipped' events
			info.events = ["failed", "skipped"]
		}

		/*
		 * Execute tests:
		 */

		super.executeTests()
	}
}
