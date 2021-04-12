package io.fabric8.kubernetes.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.fabric8.kubernetes.client.informers.cache.Lister;
import io.fabric8.openshift.api.model.ImageStream;

public class MultipleSharedInformerExample {
    private static final Logger logger = LoggerFactory.getLogger("MultiInformer::");
    private static final String NAMESPACE_PREFIX = "multi-";

    public static void main(String[] args) throws InterruptedException {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            SharedInformerFactory sharedInformerFactory = client.informers();
            Map<String, SharedIndexInformer<Pod>> namespaceToInformerMap = new HashMap<>();
            logger.info("Informer factory initialized.");
            for (int i = 10; i < 30; i++) {
                newPodSharedInformerClient(sharedInformerFactory, NAMESPACE_PREFIX + i);
                newConfigMapSharedInformerClient(sharedInformerFactory, NAMESPACE_PREFIX + i);
                newSecretSharedInformerClient(sharedInformerFactory, NAMESPACE_PREFIX + i);
                newImageStreamSharedInformerClient(sharedInformerFactory, NAMESPACE_PREFIX + i);
                // namespaceToInformerMap.put(NAMESPACE_PREFIX + i, informer);
            }
            logger.info("Starting all registered informers");
            sharedInformerFactory.startAllRegisteredInformers();

            for (Map.Entry<String, SharedIndexInformer<Pod>> entry : namespaceToInformerMap.entrySet()) {
                listPodsFromEachInformerCache(entry.getKey(), entry.getValue());
            }
            TimeUnit.MINUTES.sleep(15);
            sharedInformerFactory.stopAllRegisteredInformers();
        }
    }

    private static SharedIndexInformer<Pod> newPodSharedInformerClient(SharedInformerFactory sharedInformerFactory,
            String namespace) throws InterruptedException {
        SharedIndexInformer<Pod> informer = sharedInformerFactory.inNamespace(namespace)
                .sharedIndexInformerFor(Pod.class, 0L);
        logger.info("Registered Pod SharedInformer for {} namespace", namespace);
        informer.addEventHandler(new GenericEventHandler<Pod>());
        return informer;
    }

    private static SharedIndexInformer<Secret> newSecretSharedInformerClient(
            SharedInformerFactory sharedInformerFactory, String namespace) throws InterruptedException {
        SharedIndexInformer<Secret> informer = sharedInformerFactory.inNamespace(namespace)
                .sharedIndexInformerFor(Secret.class, 0L);
        logger.info("Registered Pod SharedInformer for {} namespace", namespace);
        informer.addEventHandler(new GenericEventHandler<Secret>());
        return informer;
    }

    private static SharedIndexInformer<ConfigMap> newConfigMapSharedInformerClient(
            SharedInformerFactory sharedInformerFactory, String namespace) throws InterruptedException {
        SharedIndexInformer<ConfigMap> informer = sharedInformerFactory.inNamespace(namespace)
                .sharedIndexInformerFor(ConfigMap.class, 0L);
        logger.info("Registered Pod SharedInformer for {} namespace", namespace);
        informer.addEventHandler(new GenericEventHandler<ConfigMap>());
        return informer;
    }

    private static SharedIndexInformer<ImageStream> newImageStreamSharedInformerClient(
            SharedInformerFactory sharedInformerFactory, String namespace) throws InterruptedException {
        SharedIndexInformer<ImageStream> informer = sharedInformerFactory.inNamespace(namespace)
                .sharedIndexInformerFor(ImageStream.class, 0L);
        logger.info("Registered Pod SharedInformer for {} namespace", namespace);
        informer.addEventHandler(new GenericEventHandler<ImageStream>());
        return informer;
    }

    private static void listPodsFromEachInformerCache(String namespace, SharedIndexInformer<Pod> podInformer) {
        // Wait till Informer syncs
        while (!podInformer.hasSynced() && !Thread.currentThread().isInterrupted())
            ;
        Lister<Pod> podLister = new Lister<>(podInformer.getIndexer(), namespace);
        logger.info("PodLister has {}", podLister.list().size());
        podLister.list().stream().map(Pod::getMetadata).map(ObjectMeta::getName)
                .forEach(p -> logger.info("{} namespace Lister list: {}", namespace, p));
    }

}