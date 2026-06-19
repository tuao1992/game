import React from 'react';

export interface WorkshopBackgroundProps extends React.HTMLAttributes<HTMLDivElement> {
  /**
   * Accent color tinting the top of the gradient — pass an environment color
   * (e.g. `var(--wr-env-kitchen)`) to theme a chapter. @default undefined
   */
  accent?: string;
  /** Minimum height of the stage. @default 320 */
  minHeight?: number | string;
}

/**
 * The shared workshop stage: a blue gradient wall dotted like pegboard, with a
 * wooden workbench along the bottom. Wrap menus, HUDs or hero art in it to drop
 * them into the game's world. `accent` tints the top to theme a chapter.
 */
export const WorkshopBackground = React.forwardRef<HTMLDivElement, WorkshopBackgroundProps>(
  function WorkshopBackground(
    { accent, minHeight = 320, className = '', style, children, ...rest },
    ref,
  ) {
    const cls = ['wr-workshop', className].filter(Boolean).join(' ');
    const mergedStyle: React.CSSProperties = {
      minHeight,
      ...(accent ? ({ ['--wr-bg-top']: accent } as React.CSSProperties) : {}),
      ...style,
    };
    return (
      <div ref={ref} className={cls} style={mergedStyle} {...rest}>
        <div className="wr-workshop__pegboard" />
        <div className="wr-workshop__bench" />
        <div className="wr-workshop__content">{children}</div>
      </div>
    );
  },
);
