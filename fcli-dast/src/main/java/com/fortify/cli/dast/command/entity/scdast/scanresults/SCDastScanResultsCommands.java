package com.fortify.cli.dast.command.entity.scdast.scanresults;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fortify.cli.common.command.util.annotation.SubcommandOf;
import com.fortify.cli.common.command.util.output.IJsonNodeTransformerSupplier;
import com.fortify.cli.common.command.util.output.OutputWriterMixin;
import com.fortify.cli.common.json.transformer.FieldBasedTransformerFactory;
import com.fortify.cli.common.json.transformer.IJsonNodeTransformer;
import com.fortify.cli.common.output.OutputFilterOptions;
import com.fortify.cli.common.output.OutputFormat;
import com.fortify.cli.common.util.JsonNodeFilterHelper;
import com.fortify.cli.dast.command.AbstractSCDastUnirestRunnerCommand;
import com.fortify.cli.dast.command.entity.SCDastEntityRootCommands;
import com.fortify.cli.dast.command.entity.scdast.scanresults.options.SCDastGetScanResultsOptions;
import com.fortify.cli.ssc.command.entity.SSCApplicationCommands;
import io.micronaut.core.annotation.ReflectiveAccess;
import kong.unirest.UnirestInstance;
import lombok.Getter;
import lombok.SneakyThrows;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SCDastScanResultsCommands {
    private static final String NAME = "scan-results";
    private static final String DESC = "DAST scan results";

    @ReflectiveAccess
    @SubcommandOf(SCDastEntityRootCommands.SCDASTGetCommand.class)
    @Command(name = NAME, description = "Get " + DESC + " from SC DAST")
    public static final class Get extends AbstractSCDastUnirestRunnerCommand implements IJsonNodeTransformerSupplier {
        @ArgGroup(exclusive = false, heading = "Get results from a specific scan:%n", order = 1)
        @Getter private SCDastGetScanResultsOptions scanResultsOptions;

        @Mixin private OutputWriterMixin outputWriterMixin;

        @ArgGroup(exclusive = false, heading = "Filter Output:%n", order = 10)
        @Getter private OutputFilterOptions outputFilterOptions;

        @SneakyThrows
        protected Void runWithUnirest(UnirestInstance unirest) {
            String urlPath = "/api/v2/scans/"+ scanResultsOptions.getScanId() + "/scan-summary";
            Set<String> outputFields = Set.of("lowCount", "mediumCount", "highCount", "criticalCount");

            JsonNode response = unirest.get(urlPath)
                    .accept("application/json")
                    .header("Content-Type", "application/json")
                    .asObject(ObjectNode.class)
                    .getBody()
                    .get("item");

            JsonNodeFilterHelper.filterJsonNode(response, outputFields);

            if (outputFilterOptions != null ){
                response = outputFilterOptions.filterOutput(response);
            }
            outputWriterMixin.printToFormat(response);

            return null;
        }

        @Override
        public IJsonNodeTransformer getJsonNodeTransformer(FieldBasedTransformerFactory fieldBasedTransformerFactory, OutputFormat format) {
            return new SSCApplicationCommands.TransformerSupplier().getJsonNodeTransformer(fieldBasedTransformerFactory, format);
        }
    }
}
