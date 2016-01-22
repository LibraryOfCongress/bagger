package gov.loc.repository.bagger.ui;

import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BagTreePanel extends JScrollPane {
  private static final long serialVersionUID = 5134745573017768256L;
  protected static final Logger log = LoggerFactory.getLogger(BagTreePanel.class);
  private BagTree bagTree;

  public BagTreePanel(BagTree bagTree) {
    super(bagTree);
    this.bagTree = bagTree;
    init();
  }

  private void init() {
    log.debug("BagTreePanel.init");
    setViewportView(bagTree);
    setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    setPreferredSize(bagTree.getTreeSize());
  }

  public void setBagTree(BagTree bagTree) {
    this.bagTree = bagTree;
  }

  public BagTree getBagTree() {
    return this.bagTree;
  }

  public void refresh(BagTree tree) {
    this.bagTree = tree;
    if (getComponentCount() > 0) {
      if (bagTree != null && bagTree.isShowing()) {
        bagTree.invalidate();
      }
    }
    init();
    invalidate();
    repaint();
  }
}
