package cn.teacy.loglens.node;

import cn.teacy.loglens.interfaces.IdProvider;
import com.alibaba.cloud.ai.graph.action.NodeAction;

public interface NodeWrapper extends IdProvider<String> {

    NodeAction getNode();

}
