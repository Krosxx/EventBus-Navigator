package cn.vove7.eventbus_nav.filter;

import com.intellij.usages.Usage;

/**
 * 过滤 Usages
 */
public interface Filter {
    boolean shouldShow(Usage usage);
}
