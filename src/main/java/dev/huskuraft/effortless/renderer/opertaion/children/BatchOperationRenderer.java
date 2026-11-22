package dev.huskuraft.effortless.renderer.opertaion.children;

import dev.huskuraft.effortless.compat.sable.SableSupport;
import dev.huskuraft.effortless.compat.sable.PreviewPoses;

import java.util.List;

import dev.huskuraft.universal.api.renderer.Renderer;
import dev.huskuraft.effortless.building.operation.batch.BatchOperationResult;
import dev.huskuraft.effortless.renderer.opertaion.OperationsRenderer;

public final class BatchOperationRenderer implements OperationRenderer {

    private final List<OperationRenderer> previews;

    public BatchOperationRenderer(OperationsRenderer operationsRenderer, BatchOperationResult result) {
        this.previews = result.getResults().stream().map(operationsRenderer::createRenderer).toList();
    }

    @Override
    public void render(Renderer renderer, RenderContext renderContext, float deltaTick) {
        if (SableSupport.LOADED) {
            PreviewPoses.duringRender(() -> renderChildren(renderer, renderContext, deltaTick));
        } else {
            renderChildren(renderer, renderContext, deltaTick);
        }
    }

    private void renderChildren(Renderer renderer, RenderContext renderContext, float deltaTick) {
        for (var preview : previews) {
            preview.render(renderer, renderContext, deltaTick);
        }
    }

}
