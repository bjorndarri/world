module is.codion.framework.demos.world.domain.api {
  requires transitive java.desktop;
  requires transitive is.codion.framework.domain;
  requires jxmapviewer2;

  exports is.codion.framework.demos.world.domain.api;

  //for accessing default methods in EntityType interfaces
  opens is.codion.framework.demos.world.domain.api;
}