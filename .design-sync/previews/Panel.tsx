import React from 'react';
import { Panel, StarRating, Button } from '@weldrite/design-system';

const Stage = ({ children }: { children: React.ReactNode }) => (
  <div
    style={{
      background: '#0e2a47',
      padding: 28,
      display: 'flex',
      gap: 18,
      flexWrap: 'wrap',
      alignItems: 'flex-start',
    }}
  >
    {children}
  </div>
);

export const Tones = () => (
  <Stage>
    <Panel tone="panel" style={{ width: 200 }}>
      <strong>Panel</strong>
      <p style={{ margin: '8px 0 0', fontWeight: 500 }}>Default workshop surface.</p>
    </Panel>
    <Panel tone="blue" style={{ width: 200 }}>
      <strong>Blue</strong>
      <p style={{ margin: '8px 0 0', fontWeight: 500 }}>Brand emphasis surface.</p>
    </Panel>
    <Panel tone="white" style={{ width: 200 }}>
      <strong>White</strong>
      <p style={{ margin: '8px 0 0', fontWeight: 500 }}>High-contrast card.</p>
    </Panel>
  </Stage>
);

export const ScoreSummary = () => (
  <Stage>
    <Panel tone="panel" elevated style={{ width: 320, textAlign: 'center' }}>
      <div style={{ fontSize: 22, fontWeight: 800 }}>Joint Complete!</div>
      <div style={{ margin: '14px 0' }}>
        <StarRating value={3} size={40} />
      </div>
      <div style={{ fontWeight: 500, color: '#9fb6ce', marginBottom: 16 }}>
        Perfect seal — 1,250 pts
      </div>
      <Button variant="success" fullWidth>
        Next Joint
      </Button>
    </Panel>
  </Stage>
);

export const Glass = () => (
  <div
    style={{
      padding: 40,
      background: 'linear-gradient(180deg,#13486f,#0e2a47)',
      display: 'flex',
      justifyContent: 'center',
    }}
  >
    <Panel tone="glass" style={{ width: 280, textAlign: 'center' }}>
      <strong>Paused</strong>
      <p style={{ margin: '8px 0 0', fontWeight: 500 }}>Frosted overlay for dialogs.</p>
    </Panel>
  </div>
);
