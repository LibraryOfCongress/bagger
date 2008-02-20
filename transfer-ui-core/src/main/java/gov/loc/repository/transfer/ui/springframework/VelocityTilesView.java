package gov.loc.repository.transfer.ui.springframework;


import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.tiles.*;
import org.apache.velocity.context.Context;
import org.springframework.context.ApplicationContextException;
import org.springframework.web.servlet.view.velocity.VelocityToolboxView;
import org.springframework.web.util.WebUtils;

public class VelocityTilesView extends VelocityToolboxView {

    /**
     * Name of the attribute that will override the path of the layout page to render. A Tiles
     * component controller can set such an attribute to dynamically switch the look and feel of a
     * Tiles page.
     * 
     * @see #setPath
     */
    public static final String PATH_ATTRIBUTE = VelocityTilesView.class.getName() + ".PATH";

    private DefinitionsFactory definitionsFactory;

    /**
     * Constructor for use as a bean.
     */
    public VelocityTilesView() {}

    /**
     * Create a new InternalResourceView with the given URL.
     */
    public VelocityTilesView(String url) {
        setUrl(url);
    }

    /**
     * Render the internal resource given the specified model. This includes setting the model as
     * request attributes.
     */
    protected void renderMergedTemplateModel(Map model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        exposeHelpers(model, request);
        Context velocityContext = createVelocityContext(model, request, response);
        exposeHelpers(velocityContext, request, response);
        exposeToolAttributes(velocityContext, request);
        exposeModelAsRequestAttributes(model, request);

        String tile = prepareForRendering(request, response);

        mergeTemplate(getTemplate(tile), velocityContext, response);
    }

    /**
     * Determine whether to use RequestDispatcher's <code>include</code> or <code>forward</code>
     * method.
     * <p>
     * Performs a check whether an include URI attribute is found in the request, indicating an
     * include request, and whether the response has already been committed. In both cases, an
     * include will be performed, as a forward is not possible anymore.
     */
    protected boolean useInclude(HttpServletRequest request, HttpServletResponse response) {
        return (WebUtils.isIncludeRequest(request) || response.isCommitted());
    }

    /**
     * Expose the current request URI and paths as {@link HttpServletRequest} attributes under the
     * keys defined in the Servlet 2.4 specification, for Servlet 2.3-containers.
     * <p>
     * Does not override values if already present, to not conflict with Servlet 2.4+ containers.
     */
    protected void exposeForwardRequestAttributes(HttpServletRequest request) {
        WebUtils.exposeForwardRequestAttributes(request);
    }

    /**
     * Overrides checkTemplate in VelocityView to avoid throwing an exception if it can't find a
     * file.
     */
    protected void checkTemplate() throws ApplicationContextException {
    // skip check.
    }

    /**
     * Set the path of the layout page to render.
     */
    public static void setPath(HttpServletRequest request, String path) {
        request.setAttribute(PATH_ATTRIBUTE, path);
    }

    protected void initApplicationContext() throws ApplicationContextException {
        super.initApplicationContext();

        // get definitions factory
        this.definitionsFactory = (DefinitionsFactory) getServletContext().getAttribute(
                TilesUtilImpl.DEFINITIONS_FACTORY);
        if (this.definitionsFactory == null) {
            throw new ApplicationContextException(
                    "Tiles definitions factory not found: TilesConfigurer not defined?");
        }
    }

    /**
     * Prepare for rendering the Tiles definition: Execute the associated component controller if
     * any, and determine the request dispatcher path.
     */
    protected String prepareForRendering(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // get component definition
        ComponentDefinition definition = getComponentDefinition(this.definitionsFactory, request);
        if (definition == null) {
            throw new ServletException("No Tiles definition found for name '" + getUrl() + "'");
        }

        // get current component context
        ComponentContext context = getComponentContext(definition, request);

        // execute component controller associated with definition, if any
        Controller controller = getController(definition, request);
        if (controller != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Executing Tiles controller [" + controller + "]");
            }
            executeController(controller, context, request, response);
        }

        // determine the path of the definition
        String path = getDispatcherPath(definition, request);
        if (path == null) {
            throw new ServletException("Could not determine a path for Tiles definition '"
                    + definition.getName() + "'");
        }

        return path;
    }

    /**
     * Determine the Tiles component definition for the given Tiles definitions factory.
     */
    protected ComponentDefinition getComponentDefinition(DefinitionsFactory factory,
            HttpServletRequest request) throws Exception {
        return factory.getDefinition(getUrl(), request, getServletContext());
    }

    /**
     * Determine the Tiles component context for the given Tiles definition.
     */
    protected ComponentContext getComponentContext(ComponentDefinition definition,
            HttpServletRequest request) throws Exception {
        ComponentContext context = ComponentContext.getContext(request);
        if (context == null) {
            context = new ComponentContext(definition.getAttributes());
            ComponentContext.setContext(context, request);
        } else {
            context.addMissing(definition.getAttributes());
        }
        return context;
    }

    /**
     * Determine and initialize the Tiles component controller for the given Tiles definition, if
     * any.
     */
    protected Controller getController(ComponentDefinition definition, HttpServletRequest request)
            throws Exception {
        return definition.getOrCreateController();
    }

    /**
     * Execute the given Tiles controller.
     */
    protected void executeController(Controller controller, ComponentContext context,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        controller.execute(context, request, response, getServletContext());
    }

    /**
     * Determine the dispatcher path for the given Tiles definition, i.e. the request dispatcher
     * path of the layout page.
     */
    protected String getDispatcherPath(ComponentDefinition definition, HttpServletRequest request)
            throws Exception {
        Object pathAttr = request.getAttribute(PATH_ATTRIBUTE);
        return (pathAttr != null ? pathAttr.toString() : definition.getPath());
    }
}