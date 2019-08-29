/*
 * Copyright (c) 2019.
 * OOON.ME ALL RIGHTS RESERVED.
 * Licensed under the Mozilla Public License, version 2.0
 * Please visit http://ooon.me or mail to zhaihao@ooon.me
 */

package sia.plot.gallery.single_view_plots

import os.RelPath
import sia.plot.gallery.Dataset
import test.BaseSpec
import plot._
import plot.spec.Themes

/**
  * ScatterStripPlotSpec
  *
  * @author zhaihao
  * @version 1.0
  * @since 2019/8/29 4:08 下午
  */
class ScatterStripPlotSpec extends BaseSpec {
  "Scatter plot" in {
    plot.vega
      .theme(Themes.Vox)
      .withUrl(Dataset.Cars)
      .viz(os.resource / RelPath("sia/plot/gallery/single_view_plots/17.json"))
      .html
      .browse()
  }

  "1D Strip Plot" in {
    plot.vega
      .theme(Themes.Vox)
      .withUrl(Dataset.Seattle_Weather)
      .viz(os.resource / RelPath("sia/plot/gallery/single_view_plots/18.json"))
      .html
      .browse()
  }

  "Strip Plot" in {
    plot.vega
      .theme(Themes.Vox)
      .withUrl(Dataset.Cars)
      .viz(os.resource / RelPath("sia/plot/gallery/single_view_plots/19.json"))
      .html
      .browse()
  }

  "Colored Scatter plot" in {
    plot.vega
      .theme(Themes.Vox)
      .withUrl(Dataset.Cars)
      .viz(os.resource / RelPath("sia/plot/gallery/single_view_plots/20.json"))
      .html
      .browse()
  }

  "Binned Scatter plot" in {
    plot.vega
      .theme(Themes.Vox)
      .withUrl(Dataset.Movies)
      .viz(os.resource / RelPath("sia/plot/gallery/single_view_plots/21.json"))
      .html
      .browse()
  }

  "Bubble Plot" in {
    plot.vega
      .theme(Themes.Vox)
      .withUrl(Dataset.Cars)
      .viz(os.resource / RelPath("sia/plot/gallery/single_view_plots/22.json"))
      .html
      .browse()
  }

  "Scatter plot with Null Values in Grey" in {
    plot.vega
      .theme(Themes.Vox)
      .withUrl(Dataset.Movies)
      .viz(os.resource / RelPath("sia/plot/gallery/single_view_plots/23.json"))
      .html
      .browse()
  }

  "Scatter plot with Filled Circles" in {
    plot.vega
      .theme(Themes.Vox)
      .withUrl(Dataset.Cars)
      .viz(os.resource / RelPath("sia/plot/gallery/single_view_plots/24.json"))
      .html
      .browse()
  }

  "Bubble Plot (Gap minder)" in {
    plot.vega
      .theme(Themes.Vox)
      .withUrl(Dataset.GapMinder)
      .viz(os.resource / RelPath("sia/plot/gallery/single_view_plots/25.json"))
      .html
      .browse()
  }

  "Bubble Plot (Natural Disasters)" in {
    plot.vega
      .theme(Themes.Vox)
      .withUrl(Dataset.Disasters)
      .viz(os.resource / RelPath("sia/plot/gallery/single_view_plots/26.json"))
      .html
      .browse()
  }

  "Scatter Plot with Text Marks" in {
    plot.vega
      .theme(Themes.Vox)
      .withUrl(Dataset.Cars)
      .viz(os.resource / RelPath("sia/plot/gallery/single_view_plots/27.json"))
      .html
      .browse()
  }
}
