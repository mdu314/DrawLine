package com.mdu.DrawLine;

public class DLColorModel {
  String _name;
  int[] _colors;
  float[] _limits;

  public String toString() {
    if (_name != null)
      return _name;
    return super.toString();
  }

  private static int getAlpha(int color) {
    return color >> 24 & 0xff;
  }

  private static int getBlue(int color) {
    return color >> 0 & 0xff;
  }

  private static int getGreen(int color) {
    return color >> 8 & 0xff;
  }

  private static int getRed(int color) {
    return color >> 16 & 0xff;
  }

  public DLColorModel(String name, int[] colors, float[] limits) {
    _name = name;
    _colors = colors;
    _limits = limits;
    validate();
  }

  public DLColorModel invert() {
    int len = _colors.length;
    for (int i = 0; i < len / 2; i++) {
      int temp = _colors[i];
      _colors[i] = _colors[len - 1 - i];
      _colors[len - 1 - i] = temp;
    }
    return this;
  }

  public void shuffle() {
    DLUtil.shuffleArray(_colors);
  }

  public DLColorModel(int[] colors, float[] limits) {
    this(null, colors, limits);
  }

  public float getAlpha(float value) {
    if (_colors == null || _colors.length == 0 || _limits == null || _limits.length == 0)
      return 1;
    final int intervalIndex = getIntervalIndex(value);
    if (intervalIndex < 0)
      return _colors[0] >> 24;

    if (intervalIndex >= _colors.length)
      return _colors[_colors.length - 1] >> 24;

    final int a1 = _colors[intervalIndex - 1] >> 24;
    final int a2 = _colors[intervalIndex] >> 24;

    final float l1 = _limits[intervalIndex - 1];
    final float l2 = _limits[intervalIndex];

    final float factor = (value - l2) / (l1 - l2);
    final float a = factor * (a1 - a2) + a2;
    return a;
  }

  public int getColor(float value) {
    if (_colors == null || _colors.length == 0)
      return 0;
    final int intervalIndex = getIntervalIndex(value);

    if (intervalIndex < 0)
      return _colors[0];

    if (intervalIndex >= _colors.length)
      return _colors[_colors.length - 1];

    final int c1 = _colors[intervalIndex - 1];
    final int c2 = _colors[intervalIndex];

    final int a1 = getAlpha(c1);
    final int a2 = getAlpha(c2);
    final int b1 = getBlue(c1);
    final int b2 = getBlue(c2);
    final int g1 = getGreen(c1);
    final int g2 = getGreen(c2);
    final int r1 = getRed(c1);
    final int r2 = getRed(c2);

    final float l1 = _limits[intervalIndex - 1];
    final float l2 = _limits[intervalIndex];

    final float factor = (value - l2) / (l1 - l2);

    final int a = (int) (factor * (a1 - a2) + a2);
    final int r = (int) (factor * (r1 - r2) + r2);
    final int g = (int) (factor * (g1 - g2) + g2);
    final int b = (int) (factor * (b1 - b2) + b2);

    final int ret = r << 16 | g << 8 | b << 0 | a << 24;
    return ret;
  }

  private int getIntervalIndex(float value) {
    final int count = _limits.length;
    if (value >= _limits[0])
      return -1;
    if (value <= _limits[count - 1])
      return count;
    for (int i = 0; i < count; i++)
      if (value >= _limits[i])
        return i;
    return count;
  }

  private void validate() {
    if (_colors != null && _limits != null && _colors.length != _limits.length)
      throw new Error("The color array must have the same size than the limmit array :" + _colors.length + " != "
          + _limits.length);

    if (_limits != null)
      for (int i = 0; i < _limits.length - 1; i++) {
        final float l1 = _limits[i];
        final float l2 = _limits[i + 1];
        if (l1 <= l2) {
          throw new Error("Limits should be in strict descending order : " + _limits);
        }
      }
  }
}
