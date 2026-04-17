package pw.tmpim.goodconfig.api

interface HierarchyNode {
  val parent: BaseContainerSpec?
  val fullPath: String
}
