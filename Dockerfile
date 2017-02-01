FROM jboss/wildfly
ADD hateoas-services/target/hateoas.war /opt/jboss/wildfly/standalone/deployments/
