package dk.nykredit.nic.rs.error;

import java.net.URI;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.UriBuilderException;

import dk.nykredit.jackson.dataformat.hal.HALLink;
import dk.nykredit.jackson.dataformat.hal.annotation.Link;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;
import dk.nykredit.nic.core.diagnostic.DiagnosticContext;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Resource
@ApiModel(value="Error", description = "a simple error representation")
/**
 * Simple Error representation
 */
public class ErrorRepresentation {
    private static final String TIME_SUBSTITUTION_KEY = "time";
    private static final String LOG_TOKEN_SUBSTITUTION_KEY = "logToken";
    private static final String DETAILS_KEY = "details";

    private String msg;
    private String sid;
    @Link
    private HALLink resource;
    @Link
    private Map<String, Object> values;

    public ErrorRepresentation() {
    }

    private ErrorRepresentation(ErrorRepresentation.Builder builder) {
        if (builder.error == null) {
            throw new IllegalArgumentException("An error code must be supplied");
        } else {
            this.values = new LinkedHashMap();
            String logToken = DiagnosticContext.getLogToken();
            this.values.put("time", builder.time);
            this.values.put("logToken", logToken);
            this.values.putAll(builder.values);
            this.sid = builder.error;
            if (builder.details != null) {
                this.values.put("details", builder.details);
            }

            this.resource = builder.resourceUri == null ? null : this.createResourceLink(builder.resourceUri);
            this.msg = builder.msg;
        }
    }

    @ApiModelProperty(
            access = "public",
            name = "message",
            example = "this error happened due to this situation",
            notes = "the contents of an error in the body as information til API consumer(s).",
            value = "Read-only")
    public String getMessage() {
        return this.msg;
    }

    @ApiModelProperty(
            access = "public",
            name = "sid",
            notes = "the semantic id of the error.",
            value = "Read-only")
    public String getSid() {
        return this.sid;
    }

    @ApiModelProperty(
            access = "public",
            name = "values",
            notes = "a collection of validation issues which caused the error.",
            value = "Read-only")
    public Map<String, Object> getValues() {
        return this.values;
    }

    @ApiModelProperty(
            access = "public",
            name = "resource",
            notes = "link to the origin of the cause of the error.")
    public HALLink getResource() {
        return this.resource;
    }


    private HALLink createResourceLink(URI uri) throws IllegalArgumentException, UriBuilderException {
        return (new dk.nykredit.jackson.dataformat.hal.HALLink.Builder(uri)).title("Link to failed resource").build();
    }

    public static class Builder {

        private String msg;
        private URI resourceUri;
        private Map<String, Object> values = new LinkedHashMap();
        private Date time = new Date();
        private String error;
        private Object details;


        public Builder(String error) {
            this.error = error;
        }

        public ErrorRepresentation.Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public ErrorRepresentation.Builder time(Date time) {
            this.time = new Date(time.getTime());
            return this;
        }

        public ErrorRepresentation.Builder resource(URI uri) {
            this.resourceUri = uri;
            return this;
        }

        public ErrorRepresentation.Builder value(String key, Object value) {
            this.values.put(key, value);
            return this;
        }

        public ErrorRepresentation.Builder details(Object details) {
            this.details = details;
            return this;
        }

        public ErrorRepresentation build() {
            return new ErrorRepresentation(this);
        }
    }
}
