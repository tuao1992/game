import React from 'react';

export type PanelTone = 'panel' | 'blue' | 'white' | 'ink' | 'glass';

export interface PanelProps extends React.HTMLAttributes<HTMLDivElement> {
  /** Surface color. @default 'panel' */
  tone?: PanelTone;
  /** Add the soft drop shadow for a raised card. @default false */
  elevated?: boolean;
}

/**
 * Rounded surface/card that groups content — score summaries, settings rows,
 * dialogs. Tones map to the game's panels; `elevated` adds a soft drop shadow.
 */
export const Panel = React.forwardRef<HTMLDivElement, PanelProps>(function Panel(
  { tone = 'panel', elevated = false, className = '', children, ...rest },
  ref,
) {
  const cls = [
    'wr-panel',
    tone !== 'panel' ? `wr-panel--${tone}` : '',
    elevated ? 'wr-panel--elevated' : '',
    className,
  ]
    .filter(Boolean)
    .join(' ');
  return (
    <div ref={ref} className={cls} {...rest}>
      {children}
    </div>
  );
});
