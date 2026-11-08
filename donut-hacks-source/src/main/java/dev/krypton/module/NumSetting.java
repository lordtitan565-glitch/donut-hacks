package dev.krypton.module;

public class NumSetting {
    public final String name;
    public double value;
    public final double min, max, step;
    public String[] labels;

    public NumSetting(String name, double value, double min, double max, double step) {
        this.name = name; this.value = value; this.min = min; this.max = max; this.step = step;
    }

    public void cycle() {
        value += step;
        if (value > max + 1e-9) value = min;
    }

    public String label() {
        if (labels != null) {
            int i = Math.max(0, Math.min(labels.length - 1, (int) Math.round(value)));
            return name + ": " + labels[i];
        }
        return name + ": " + (Math.abs(value - Math.round(value)) < 1e-9 ? String.valueOf((long) Math.round(value)) : String.format("%.2f", value));
    }
}
