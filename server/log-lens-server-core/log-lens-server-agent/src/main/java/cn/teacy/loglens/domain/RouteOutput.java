package cn.teacy.loglens.domain;

import lombok.Data;

@Data
public class RouteOutput {

    private boolean triggerAlert;

    private String summary;

    private String reasoning;

}
