package org.nrg.plexiviewer.utils;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ij.util.StringSorter;
import ij.util.Tools;

public class DicomSorter {
    static final int MAX_DIGITS = 5;

    public void run(String arg) {
        ImagePlus imp = IJ.getImage();
        if(!this.isDicomStack(imp)) {
            IJ.showMessage("DICOM Sorter", "This command requires a DICOM stack");
        } else {
            int stackSize = imp.getStackSize();
            ImageStack stack = imp.getStack();
            String[] strings = this.getSortStrings(stack, "0020,0013");
            if(strings != null) {
                StringSorter.sort(strings);
                ImageStack stack2 = this.sortStack(stack, strings);
                if(stack2 != null) {
                    imp.setStack((String)null, stack2);
                }

            }
        }
    }

    public ImageStack sort(ImageStack stack) {
        if(IJ.debugMode) {
            IJ.log("DICOM_Sorter: sorting by image number");
        }

        if(stack.getSize() == 1) {
            return stack;
        } else {
            String[] strings = this.getSortStrings(stack, "0020,0013");
            if(strings == null) {
                return stack;
            } else {
                StringSorter.sort(strings);
                ImageStack stack2 = this.sortStack(stack, strings);
                return stack2 != null?stack2:stack;
            }
        }
    }

    ImageStack sortStack(ImageStack stack, String[] strings) {
        ImageProcessor ip = stack.getProcessor(1);
        ImageStack stack2 = new ImageStack(ip.getWidth(), ip.getHeight(), ip.getColorModel());

        for(int i = 0; i < stack.getSize(); ++i) {
            int slice = (int) Tools.parseDouble(strings[i].substring(strings[i].length() - 5), 0.0D);
            if(slice == 0) {
                return null;
            }

            stack2.addSlice(stack.getSliceLabel(slice), stack.getPixels(slice));
        }

        stack2.update(stack.getProcessor(1));
        return stack2;
    }

    String[] getSortStrings(ImageStack stack, String tag) {
        double series = this.getSeriesNumber(stack.getSliceLabel(1));
        int n = stack.getSize();
        String[] values = new String[n];

        for(int i = 1; i <= n; ++i) {
            String tags = stack.getSliceLabel(i);
            if(tags == null) {
                return null;
            }

            double value = this.getNumericTag(tags, tag);
            if(Double.isNaN(value)) {
                if(IJ.debugMode) {
                    IJ.log("  " + tag + "  tag missing in slice " + i);
                }

                return null;
            }

            if(this.getSeriesNumber(tags) != series) {
                if(IJ.debugMode) {
                    IJ.log("  all slices must be part of the same series");
                }

                return null;
            }

            values[i - 1] = this.toString(value, 5) + this.toString((double)i, 5);
        }

        return values;
    }

    String toString(double value, int width) {
        String s = "       " + IJ.d2s(value, 0);
        return s.substring(s.length() - 5);
    }

    boolean isDicomStack(ImagePlus imp) {
        if(imp.getStackSize() == 1) {
            return false;
        } else {
            ImageStack stack = imp.getStack();
            String label = stack.getSliceLabel(1);
            return label != null && label.lastIndexOf("7FE0,0010") > 0;
        }
    }

    double getSeriesNumber(String tags) {
        double series = this.getNumericTag(tags, "0020,0011");
        if(Double.isNaN(series)) {
            series = 0.0D;
        }

        return series;
    }

    double getNumericTag(String hdr, String tag) {
        String value = this.getTag(hdr, tag);
        if(value.equals("")) {
            return 0.0D / 0.0;
        } else {
            int index3 = value.indexOf("\\");
            if(index3 > 0) {
                value = value.substring(0, index3);
            }

            return Tools.parseDouble(value);
        }
    }

    String getTag(String hdr, String tag) {
        if(hdr == null) {
            return "";
        } else {
            int index1 = hdr.indexOf(tag);
            if(index1 == -1) {
                return "";
            } else {
                if(hdr.charAt(index1 + 11) == 62) {
                    index1 = hdr.indexOf(tag, index1 + 10);
                    if(index1 == -1) {
                        return "";
                    }
                }

                index1 = hdr.indexOf(":", index1);
                if(index1 == -1) {
                    return "";
                } else {
                    int index2 = hdr.indexOf("\n", index1);
                    String value = hdr.substring(index1 + 1, index2);
                    return value;
                }
            }
        }
    }
}
