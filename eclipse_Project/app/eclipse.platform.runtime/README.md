Contributing to Eclipse Platform runtime project
================================================

Thanks for your interest in this project.

Project description:
--------------------

Platform runtime provides the background componentns for Eclipse based applications.


Website: <https://projects.eclipse.org/projects/eclipse.platform>

For more information, refer to the [Platform UI wiki page][1].

How to contribute:
--------------------
Contributions to Platform UI are most welcome. There are many ways to contribute,
from entering high quality bug reports, to contributing code or documentation changes.
For a complete guide, see the [Platform UI - How to contribute wiki page][2] page on the team wiki.

Test dependencies
-----------------

Several test plug-ins have a dependency to the Mockito and Hamcrest library.
Please install them from the [Orbit Download page][3]

Currently the following versions are required:

- org.hamcrest;bundle-version="1.3.0",
- org.mockito;bundle-version="2.13",



How to build on the command line
--------------------------------

You need Maven 3.3.1 installed. After this you can run the build via the following command:

mvn clean verify -Pbuild-individual-bundles


Developer resources:
--------------------

Information regarding source code management, builds, coding standards, and more.

- <https://projects.eclipse.org/projects/eclipse.platform/developer>

Contributor License Agreement:
------------------------------

Before your contribution can be accepted by the project, you need to create and electronically sign the Eclipse Foundation Contributor License Agreement (CLA).

- <http://www.eclipse.org/legal/CLA.php>


Search for bugs:
----------------

This project uses Bugzilla to track ongoing development and issues.

- <https://bugs.eclipse.org/bugs/buglist.cgi?bug_status=UNCONFIRMED&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&classification=Eclipse&component=runtime&list_id=12049886&product=Platform&query_format=advanced>

Create a new bug:
-----------------

Be sure to search for existing bugs before you create another one. Remember that contributions are always welcome!

- <https://bugs.eclipse.org/bugs/enter_bug.cgi?product=platform;component=runtime>

Contact:
--------

Contact the project developers via the project's "dev" list.

- <https://accounts.eclipse.org/mailing-list/platform-dev>


License
-------

[Eclipse Public License (EPL) 2.0][4]

[1]: http://wiki.eclipse.org/Platform_UI
[2]: https://wiki.eclipse.org/Platform_UI/How_to_Contribute
[3]: http://download.eclipse.org/tools/orbit/downloads/
[4]: https://www.eclipse.org/legal/epl-2.0/
