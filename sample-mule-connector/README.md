
Sample devkit cloud connector WITHOUT @Source
=======

devkit command used to generate project -

    mvn archetype:generate -DarchetypeGroupId=org.mule.tools.devkit \
      -DarchetypeArtifactId=mule-devkit-archetype-cloud-connector \
      -DarchetypeVersion=3.3.2 -DgroupId=com.espn.mule.connector.sample \
      -DartifactId=sample-mule-connector -Dversion=1.0-SNAPSHOT \
      -DmuleVersion=3.3.0 -DmuleConnectorName=Sample -Dpackage=com.espn.mule.connector.sample \
      -DarchetypeRepository=http://repository.mulesoft.org/releases
