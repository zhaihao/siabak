/*
 * Copyright (c) 2019.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package sia.akka.cluster.cluster_singleton

import akka.actor.{PoisonPill, Props}
import akka.cluster.Cluster
import akka.persistence.journal.leveldb.SharedLeveldbStore
import akka.persistence.{PersistentActor, SnapshotOffer}
import akka.remote.testkit.{MultiNodeConfig, MultiNodeSpec}
import akka.testkit.ImplicitSender
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import org.jboss.netty.logging.{InternalLoggerFactory, Slf4JLoggerFactory}
import sia.akka.STMultiNodeSpec

/**
  * ClusterSingletonSpec
  *
  * @author zhaihao
  * @version 1.0
  * @since 2019-08-06 19:29
  */
class ClusterSingletonSpec
    extends MultiNodeSpec(ClusterSingletonSpec)
    with STMultiNodeSpec
    with ImplicitSender
    with StrictLogging {

  override def initialParticipants = roles.size
  import ClusterSingletonSpec._

  "test" in {
    val cluster = Cluster(system)
    cluster.join(node(node1).address) // 手动设置 seed
    enterBarrier("up")

    runOn(node1){
      system.actorOf(Props[SharedLeveldbStore],"store")
    }

    runOn((nodes - node1):_*){

    }
  }

}

object ClusterSingletonSpec extends MultiNodeConfig {
  val nodes @ Seq(node1, node2, node3, node4, node5) = (1 to 5).map(i => role(s"node$i"))

  // Fix to avoid 'java.util.concurrent.RejectedExecutionException: Worker has already been shutdown'
  InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory)

  nodes.foreach { node =>
    val i    = node.name.last.toString.toInt
    val port = 25500 + i
    nodeConfig(node)(
      ConfigFactory.parseString(
        //language=HOCON
        s"""
           |akka.remote.netty.tcp.host = "127.0.0.1"
           |akka.remote.netty.tcp.port = "$port"
           |""".stripMargin))
  }

  commonConfig(
    ConfigFactory.parseString(
      //language=HOCON
      """
        |akka {
        |  loggers = ["akka.event.slf4j.Slf4jLogger"]
        |  loglevel = "DEBUG"
        |  log-dead-letters-during-shutdown = false
        |  log-dead-letters = 0
        |  actor {
        |    provider = cluster
        |  }
        |
        |  remote {
        |    enabled-transports = [akka.remote.netty.tcp]
        |    use-passive-connections = off
        |  }
        |
        |  cluster {
        |    roles = [compute]
        |  }
        |  persistence {
        |    journal{
        |      plugin = "akka.persistence.journal.leveldb-shared"
        |      leveldb-shared.store {
        |        native = off
        |        dir = "target/shared-journal"
        |      }
        |    }
        |    snapshot-store{
        |      plugin = "akka.persistence.snapshot-store.local"
        |      local.dir = "target/snapshots"
        |    }
        |  }
        |}
        |""".stripMargin))
}

class ClusterSingletonSpecMultiJvm1 extends ClusterSingletonSpec
class ClusterSingletonSpecMultiJvm2 extends ClusterSingletonSpec
class ClusterSingletonSpecMultiJvm3 extends ClusterSingletonSpec
class ClusterSingletonSpecMultiJvm4 extends ClusterSingletonSpec
class ClusterSingletonSpecMultiJvm5 extends ClusterSingletonSpec

private[cluster_singleton] class SingletonActor extends PersistentActor with StrictLogging {

  val cluster = Cluster(context.system)

  var freeHoles  = 0 // 没有种树的洞
  var freeTrees  = 0 // 还没有种的树
  var ttlMatches = 0 // 种好的

  def updateState(evt: Event) = evt match {
    case AddHole =>
      if (freeTrees > 0) {
        ttlMatches += 1
        freeTrees  -= 1
      } else freeHoles += 1
    case AddTree =>
      if (freeHoles > 0) {
        ttlMatches += 1
        freeHoles  -= 1
      } else freeTrees += 1
  }

  override def receiveRecover = {
    case event: Event => updateState(event)
    case SnapshotOffer(_, s: State) =>
      freeHoles  = s.nHoles
      freeTrees  = s.nTrees
      ttlMatches = s.nMatches
  }

  override def receiveCommand = {
    case Dig =>
      persist(AddHole) { event =>
        updateState(event)
      }
      sender() ! AckDig
      logger.trace(
        s"State on ${cluster.selfAddress}:freeHoles=$freeHoles,freeTrees=$freeTrees,ttlMatches=$ttlMatches")

    case Plant =>
      persist(AddTree) { event =>
        updateState(event)
      }
      sender() ! AckPlant
      logger.trace(
        s"State on ${cluster.selfAddress}:freeHoles=$freeHoles,freeTrees=$freeTrees,ttlMatches=$ttlMatches")

    case Disconnect =>
      logger.trace(s"${cluster.selfAddress} is leaving cluster ...")
      cluster.leave(cluster.selfAddress)

    case CleanUp => self ! PoisonPill
  }

  override def persistenceId = self.path.parent.name + "-" + self.path.name

}

private[cluster_singleton] sealed trait Command
private[cluster_singleton] case object Dig        extends Command
private[cluster_singleton] case object Plant      extends Command
private[cluster_singleton] case object AckDig     extends Command
private[cluster_singleton] case object AckPlant   extends Command
private[cluster_singleton] case object Disconnect extends Command
private[cluster_singleton] case object CleanUp    extends Command

private[cluster_singleton] sealed trait Event
private[cluster_singleton] case object AddHole extends Event
private[cluster_singleton] case object AddTree extends Event

private[cluster_singleton] case class State(nHoles: Int, nTrees: Int, nMatches: Int)