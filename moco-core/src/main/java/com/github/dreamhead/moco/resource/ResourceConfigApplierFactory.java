package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;
import com.google.common.collect.ImmutableMap;

import java.io.File;

import static com.github.dreamhead.moco.resource.ResourceFactory.*;

public class ResourceConfigApplierFactory {
    public static ResourceConfigApplier DO_NOTHING_APPLIER = new ResourceConfigApplier() {
        @Override
        public Resource apply(MocoConfig config, Resource resource) {
            return resource;
        }
    };

    public static ResourceConfigApplier fileConfigApplier(final String id, final File file) {
        return new SelfResourceConfigApplier(id) {
            @Override
            protected Resource newResource(MocoConfig config) {
                return fileResource(new File(config.apply(file.getName())));
            }
        };
    }

    public static ResourceConfigApplier cookieConfigApplier(final String key, final Resource cookieResource) {
        return new EmbeddedResourceConfigApplier(cookieResource) {
            @Override
            protected Resource newResource(MocoConfig config) {
                return cookieResource(key, cookieResource.apply(config));
            }
        };
    }

    public static ResourceConfigApplier templateConfigApplier(final ContentResource template, final ImmutableMap<String, Object> variables) {
        return new EmbeddedResourceConfigApplier(template) {
            @Override
            protected Resource newResource(MocoConfig config) {
                return templateResource((ContentResource) template.apply(config), variables);
            }
        };
    }

    public static ResourceConfigApplier uriConfigApplier(final String id, final String uri) {
        return new SelfResourceConfigApplier(id) {
            @Override
            protected Resource newResource(MocoConfig config) {
                return uriResource(config.apply(uri));
            }
        };
    }

    private static abstract class BaseResourceConfigAppllier implements ResourceConfigApplier {
        protected abstract Resource newResource(MocoConfig config);
        protected abstract String id();

        @Override
        public Resource apply(MocoConfig config, Resource resource) {
            if (config.isFor(id())) {
                return newResource(config);
            }

            return resource;
        }
    }

    private static abstract class SelfResourceConfigApplier extends BaseResourceConfigAppllier {
        private String id;

        private SelfResourceConfigApplier(String id) {
            this.id = id;
        }

        @Override
        protected String id() {
            return id;
        }
    }

    private static abstract class EmbeddedResourceConfigApplier extends BaseResourceConfigAppllier {
        private Resource resource;

        private EmbeddedResourceConfigApplier(Resource resource) {
            this.resource = resource;
        }

        @Override
        protected String id() {
            return resource.id();
        }
    }

    private ResourceConfigApplierFactory() {}
}
