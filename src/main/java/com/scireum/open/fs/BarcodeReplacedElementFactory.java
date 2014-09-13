package com.scireum.open.fs;

import java.awt.Color;

import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextReplacedElementFactory;
import org.xhtmlrenderer.render.BlockBox;

import com.lowagie.text.Image;
import com.lowagie.text.pdf.Barcode128;
import com.scireum.open.commons.Tuple;

public class BarcodeReplacedElementFactory extends ITextReplacedElementFactory {

	public BarcodeReplacedElementFactory(ITextOutputDevice outputDevice) {
		super(outputDevice);
	}

	@Override
	public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box,
			UserAgentCallback uac, int cssWidth, int cssHeight) {

		Element e = box.getElement();
		if (e == null) {
			return null;
		}

		String nodeName = e.getNodeName();
		if (nodeName.equals("img")) {
			if ("code128".equals(e.getAttribute("type"))) {
				try {
					Barcode128 code = new Barcode128();
					code.setCode(e.getAttribute("src"));
					FSImage fsImage = new ITextFSImage(Image.getInstance(
							code.createAwtImage(Color.BLACK, Color.WHITE),
							Color.WHITE));
					if (cssWidth != -1 || cssHeight != -1) {
						fsImage.scale(cssWidth, cssHeight);
					}
					return new ITextImageElement(fsImage);
				} catch (Throwable e1) {
					return null;
				}
			} else {
				FSImage fsImage = uac.getImageResource(e.getAttribute("src"))
						.getImage();
				if (fsImage != null) {
					if (cssWidth != -1 || cssHeight != -1) {
						Tuple<Integer, Integer> newSize = computeResizeBox(
								cssWidth, cssHeight, fsImage);
						if (newSize != null) {
							fsImage.scale(newSize.getFirst(),
									newSize.getSecond());
						}
					}
					return new ITextImageElement(fsImage);
				}
			}
		}

		return super.createReplacedElement(c, box, uac, cssWidth, cssHeight);
	}

	private Tuple<Integer, Integer> computeResizeBox(int cssWidth,
			int cssHeight, FSImage fsImage) {
		if (cssWidth == -1 && cssHeight == -1) {
			return null;
		}

		int newWidth = -1;
		int newHeight = fsImage.getHeight();

		// Downsize an maintain aspect ratio...
		if (fsImage.getWidth() > cssWidth && cssWidth > -1) {
			newWidth = cssWidth;
			newHeight = (newWidth * fsImage.getHeight()) / fsImage.getWidth();
		}

		if (cssHeight > -1 && newHeight > cssHeight) {
			newHeight = cssHeight;
			newWidth = (newHeight * fsImage.getWidth()) / fsImage.getHeight();
		}

		// No resize required
		if (newWidth == -1) {
			return null;
		}

		// No upscaling!
		if (newWidth > fsImage.getWidth() || newWidth > fsImage.getHeight()) {
			return null;
		}

		return new Tuple<Integer, Integer>(newWidth, newHeight);
	}
}
