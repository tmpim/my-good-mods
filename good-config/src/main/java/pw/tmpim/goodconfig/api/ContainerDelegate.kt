package pw.tmpim.goodconfig.api

interface ContainerDelegate<T : BaseContainerSpec> {
  val spec: T
}
