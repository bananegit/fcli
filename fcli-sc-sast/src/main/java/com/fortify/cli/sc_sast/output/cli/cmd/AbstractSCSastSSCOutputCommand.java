package com.fortify.cli.sc_sast.output.cli.cmd;

import java.util.function.Function;

import com.fortify.cli.common.output.cli.cmd.unirest.AbstractUnirestOutputCommand;
import com.fortify.cli.sc_sast.rest.cli.mixin.SCSastControllerUnirestRunnerMixin;

import io.micronaut.core.annotation.ReflectiveAccess;
import kong.unirest.UnirestInstance;
import lombok.Getter;
import picocli.CommandLine.Mixin;

@ReflectiveAccess
public abstract class AbstractSCSastSSCOutputCommand extends AbstractUnirestOutputCommand {
    @Getter @Mixin SCSastControllerUnirestRunnerMixin unirestRunner;
    
    public final <R> R runOnSSC(Function<UnirestInstance, R> f) {
        return unirestRunner.runOnSSC(f);
    }
    
    public final <R> R runOnController(Function<UnirestInstance, R> f) {
        return unirestRunner.runOnController(f);
    }
}
