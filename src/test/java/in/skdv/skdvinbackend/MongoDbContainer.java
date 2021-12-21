package in.skdv.skdvinbackend;

import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;


/**
 * This {@link Container} is based on the official MongoDb ({@code mongo}) image from
 * <a href="https://hub.docker.com/r/_/mongo/">DockerHub</a>. If you need to use a custom MongoDB
 * image, you can provide the full image name as well.
 *
 * @author Stefan Ludwig
 */
public class MongoDbContainer extends GenericContainer<MongoDbContainer> {

    /**
     * This is the internal port on which MongoDB is running inside the container.
     * <p>
     * You can use this constant in case you want to map an explicit public port to it
     * instead of the default random port. This can be done using methods like
     * {@link #setPortBindings(java.util.List)}.
     */
    public static final int MONGODB_PORT = 27017;
    public static final String DEFAULT_IMAGE_AND_TAG = "mongo:4.4";

    /**
     * Creates a new {@link MongoDbContainer} with the {@value DEFAULT_IMAGE_AND_TAG} image.
     */
    public MongoDbContainer() {
        this(DockerImageName.parse(DEFAULT_IMAGE_AND_TAG));
    }

    /**
     * Creates a new {@link MongoDbContainer} with the specified image.
     */
    public MongoDbContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        addExposedPort(MONGODB_PORT);
    }

}