import {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
  type KeyboardEvent as ReactKeyboardEvent,
  type PointerEvent as ReactPointerEvent,
  type WheelEvent as ReactWheelEvent,
} from 'react';
import {
  EXPLORATION_REGIONS,
  INITIAL_COMPANIONS,
  NATIVE_MAP_HEIGHT,
  NATIVE_MAP_WIDTH,
  type ExplorationRegion,
  type LevelInfo,
} from '../data/models';
import {
  CenterTargetIcon,
  CloseIcon,
  CompassIcon,
  FlagIcon,
  HelpIcon,
  LockIcon,
  MapIcon,
  ResetIcon,
  StarIcon,
  ZoomInIcon,
  ZoomOutIcon,
} from './ExpeditionIcons';

interface IsometricWorldMapProps {
  levels: LevelInfo[];
  activeCompanionId: string;
  isRegionUnlocked: (stageNumber: number) => boolean;
  onLevelClick: (level: LevelInfo) => void;
  onRegionClick: (region: ExplorationRegion) => void;
  onReplayGuide: () => void;
  totalStars: number;
  completedCount: number;
}

const MAP_ASPECT_RATIO = NATIVE_MAP_WIDTH / NATIVE_MAP_HEIGHT;

export function IsometricWorldMap({
  levels,
  activeCompanionId,
  isRegionUnlocked,
  onLevelClick,
  onRegionClick,
  onReplayGuide,
  totalStars,
  completedCount,
}: IsometricWorldMapProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const [viewport, setViewport] = useState({ width: 390, height: 680 });
  const [scale, setScale] = useState(1.0);
  const [offset, setOffset] = useState({ x: 0, y: 0 });
  const [isDragging, setIsDragging] = useState(false);
  const [showLegend, setShowLegend] = useState(false);
  const [lockedAlert, setLockedAlert] = useState<string | null>(null);

  const activePointers = useRef<Map<number, { x: number; y: number }>>(new Map());
  const lastDragPoint = useRef<{ x: number; y: number } | null>(null);
  const lastPinchDist = useRef<number | null>(null);

  const activeLevel = useMemo(() => {
    const available = levels.filter((lvl) => lvl.status === 'AVAILABLE');
    if (available.length > 0) return available[available.length - 1];
    const completed = levels.filter((lvl) => lvl.status === 'COMPLETED');
    if (completed.length > 0) return completed[completed.length - 1];
    return levels[0];
  }, [levels]);

  const activeStageNumber = activeLevel?.stageNumber ?? 1;

  const companionEmoji = useMemo(
    () => INITIAL_COMPANIONS.find((c) => c.id === activeCompanionId)?.emoji ?? '🦊',
    [activeCompanionId]
  );

  // Measure available map stage via ResizeObserver
  useEffect(() => {
    const el = containerRef.current;
    if (!el) return;

    const updateSize = () => {
      const rect = el.getBoundingClientRect();
      if (rect.width > 0 && rect.height > 0) {
        setViewport({ width: rect.width, height: rect.height });
      }
    };

    updateSize();
    const observer = new ResizeObserver(updateSize);
    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  // Preserve 9:16 portrait artwork without distortion (object-fit: contain math)
  const fittedSize = useMemo(() => {
    const containerAspect = viewport.width / Math.max(viewport.height, 1);
    if (containerAspect > MAP_ASPECT_RATIO) {
      const h = viewport.height;
      const w = h * MAP_ASPECT_RATIO;
      return { width: w, height: h };
    }
    const w = viewport.width;
    const h = w / MAP_ASPECT_RATIO;
    return { width: w, height: h };
  }, [viewport]);

  const clampPan = useCallback(
    (rawX: number, rawY: number, currentScale: number) => {
      const renderedW = fittedSize.width * currentScale;
      const renderedH = fittedSize.height * currentScale;

      const minX =
        renderedW > viewport.width ? viewport.width - renderedW : (viewport.width - renderedW) / 2;
      const maxX = renderedW > viewport.width ? 0 : (viewport.width - renderedW) / 2;

      const minY =
        renderedH > viewport.height
          ? viewport.height - renderedH
          : (viewport.height - renderedH) / 2;
      const maxY = renderedH > viewport.height ? 0 : (viewport.height - renderedH) / 2;

      return {
        x: Math.min(Math.max(rawX, minX), maxX),
        y: Math.min(Math.max(rawY, minY), maxY),
      };
    },
    [fittedSize, viewport]
  );

  const focusMapCoord = useCallback(
    (mapX: number, mapY: number, targetScale: number) => {
      const clampedScale = Math.min(Math.max(targetScale, 1), 3);
      const normX = (mapX / NATIVE_MAP_WIDTH) * fittedSize.width;
      const normY = (mapY / NATIVE_MAP_HEIGHT) * fittedSize.height;
      const targetX = viewport.width / 2 - normX * clampedScale;
      const targetY = viewport.height * 0.62 - normY * clampedScale;
      setScale(clampedScale);
      setOffset(clampPan(targetX, targetY, clampedScale));
    },
    [clampPan, fittedSize, viewport]
  );

  // Center active level cleanly on open & resize
  useEffect(() => {
    if (!activeLevel) return;
    const baseScale = viewport.width >= 1024 ? 1.25 : 1.0;
    const normX = (activeLevel.posX / NATIVE_MAP_WIDTH) * fittedSize.width;
    const normY = (activeLevel.posY / NATIVE_MAP_HEIGHT) * fittedSize.height;
    const cx = viewport.width / 2 - normX * baseScale;
    const cy = viewport.height * 0.68 - normY * baseScale;
    setScale(baseScale);
    setOffset(clampPan(cx, cy, baseScale));
  }, [activeLevel, clampPan, fittedSize.width, fittedSize.height, viewport.width, viewport.height]);

  useEffect(() => {
    if (!lockedAlert) return;
    const timer = window.setTimeout(() => setLockedAlert(null), 2800);
    return () => window.clearTimeout(timer);
  }, [lockedAlert]);

  const applyZoom = useCallback(
    (factor: number, anchorX = viewport.width / 2, anchorY = viewport.height / 2) => {
      setScale((prevScale) => {
        const nextScale = Math.min(Math.max(prevScale * factor, 1), 3);
        const ratio = nextScale / prevScale;
        setOffset((prevOffset) => {
          const nextX = anchorX - (anchorX - prevOffset.x) * ratio;
          const nextY = anchorY - (anchorY - prevOffset.y) * ratio;
          return clampPan(nextX, nextY, nextScale);
        });
        return nextScale;
      });
    },
    [clampPan, viewport.height, viewport.width]
  );

  const handleWheel = (event: ReactWheelEvent<HTMLDivElement>) => {
    event.preventDefault();
    const rect = containerRef.current?.getBoundingClientRect();
    const anchorX = rect ? event.clientX - rect.left : viewport.width / 2;
    const anchorY = rect ? event.clientY - rect.top : viewport.height / 2;
    const factor = event.deltaY < 0 ? 1.15 : 0.87;
    applyZoom(factor, anchorX, anchorY);
  };

  const handlePointerDown = (event: ReactPointerEvent<HTMLDivElement>) => {
    if ((event.target as HTMLElement).closest('button')) return;
    activePointers.current.set(event.pointerId, { x: event.clientX, y: event.clientY });

    if (activePointers.current.size === 1) {
      lastDragPoint.current = { x: event.clientX, y: event.clientY };
      setIsDragging(true);
    } else if (activePointers.current.size === 2) {
      const pts = Array.from(activePointers.current.values());
      lastPinchDist.current = Math.hypot(pts[0].x - pts[1].x, pts[0].y - pts[1].y);
    }
  };

  const handlePointerMove = (event: ReactPointerEvent<HTMLDivElement>) => {
    if (!activePointers.current.has(event.pointerId)) return;
    activePointers.current.set(event.pointerId, { x: event.clientX, y: event.clientY });

    if (activePointers.current.size === 2 && lastPinchDist.current !== null) {
      const pts = Array.from(activePointers.current.values());
      const dist = Math.hypot(pts[0].x - pts[1].x, pts[0].y - pts[1].y);
      if (dist > 0) {
        const factor = dist / lastPinchDist.current;
        const midX = (pts[0].x + pts[1].x) / 2;
        const midY = (pts[0].y + pts[1].y) / 2;
        const rect = containerRef.current?.getBoundingClientRect();
        applyZoom(factor, rect ? midX - rect.left : viewport.width / 2, rect ? midY - rect.top : viewport.height / 2);
        lastPinchDist.current = dist;
      }
      return;
    }

    if (activePointers.current.size === 1 && lastDragPoint.current) {
      const dx = event.clientX - lastDragPoint.current.x;
      const dy = event.clientY - lastDragPoint.current.y;
      lastDragPoint.current = { x: event.clientX, y: event.clientY };
      setOffset((prev) => clampPan(prev.x + dx, prev.y + dy, scale));
    }
  };

  const handlePointerUp = (event: ReactPointerEvent<HTMLDivElement>) => {
    activePointers.current.delete(event.pointerId);
    if (activePointers.current.size < 2) {
      lastPinchDist.current = null;
    }
    if (activePointers.current.size === 0) {
      lastDragPoint.current = null;
      setIsDragging(false);
    }
  };

  const handleKeyDown = (event: ReactKeyboardEvent<HTMLDivElement>) => {
    if ((event.target as HTMLElement).tagName === 'BUTTON') return;
    const step = 48;
    switch (event.key) {
      case 'ArrowUp':
        event.preventDefault();
        setOffset((prev) => clampPan(prev.x, prev.y + step, scale));
        break;
      case 'ArrowDown':
        event.preventDefault();
        setOffset((prev) => clampPan(prev.x, prev.y - step, scale));
        break;
      case 'ArrowLeft':
        event.preventDefault();
        setOffset((prev) => clampPan(prev.x + step, prev.y, scale));
        break;
      case 'ArrowRight':
        event.preventDefault();
        setOffset((prev) => clampPan(prev.x - step, prev.y, scale));
        break;
      case '+':
      case '=':
        event.preventDefault();
        applyZoom(1.2);
        break;
      case '-':
      case '_':
        event.preventDefault();
        applyZoom(0.82);
        break;
      case '0':
        event.preventDefault();
        setScale(1.0);
        setOffset(clampPan(0, 0, 1.0));
        break;
    }
  };

  return (
    <div
      ref={containerRef}
      className={`map-gesture-stage ${isDragging ? 'is-dragging' : ''}`}
      tabIndex={0}
      role="region"
      aria-label="Kanvas Peta Petualangan interaktif. Gunakan mouse drag, gulir roda zoom, atau tombol panah keyboard."
      onWheel={handleWheel}
      onPointerDown={handlePointerDown}
      onPointerMove={handlePointerMove}
      onPointerUp={handlePointerUp}
      onPointerCancel={handlePointerUp}
      onKeyDown={handleKeyDown}
    >
      {/* Top HUD: Stage Jump Strip + Stars/Progress Pill */}
      <div className="map-hud-top">
        <div className="map-stage-strip" role="toolbar" aria-label="Lompat cepat ke wilayah peta">
          {EXPLORATION_REGIONS.map((region) => {
            const unlocked = isRegionUnlocked(region.stageNumber);
            const isCurrent = region.stageNumber === activeStageNumber;
            return (
              <button
                key={region.stageNumber}
                type="button"
                className={`stage-jump-btn ${isCurrent ? 'is-active' : !unlocked ? 'is-locked' : ''}`}
                data-testid={`jump_stage_${region.stageNumber}`}
                onClick={() => focusMapCoord(region.pixelX, region.pixelY, viewport.width >= 1024 ? 1.55 : 1.4)}
              >
                <span aria-hidden="true">{region.iconEmoji}</span>
                <span>{region.shortName}</span>
                {isCurrent && <CompassIcon size={12} />}
                {!unlocked && <LockIcon size={12} />}
              </button>
            );
          })}
        </div>

        <div className="map-stats-pill" aria-label={`Total bintang ${totalStars}, selesai ${completedCount} dari 50 level`}>
          <span>⭐ {totalStars}</span>
          <span>🏆 {completedCount}/50</span>
        </div>
      </div>

      {/* Scaled Map Canvas preserving 9:16 illustration geometry */}
      <div
        className="map-transform-layer"
        style={{
          width: `${fittedSize.width}px`,
          height: `${fittedSize.height}px`,
          transform: `translate3d(${offset.x}px, ${offset.y}px, 0) scale(${scale})`,
        }}
      >
        <img
          src="/assets/peta_petualangan_portrait.jpg"
          alt="Ilustrasi Peta Pulau Petualangan Word Explorer"
          className="map-artwork-img"
          width={NATIVE_MAP_WIDTH}
          height={NATIVE_MAP_HEIGHT}
          decoding="async"
          {...({ fetchpriority: 'high' } as Record<string, string>)}
          draggable={false}
        />

        {/* SVG Trail connecting all 50 calibrated nodes */}
        <svg
          className="map-trail-svg"
          viewBox={`0 0 ${NATIVE_MAP_WIDTH} ${NATIVE_MAP_HEIGHT}`}
          preserveAspectRatio="none"
          aria-hidden="true"
        >
          {levels.slice(0, -1).map((curr, i) => {
            const next = levels[i + 1];
            const isOpen = curr.status === 'COMPLETED' || next.status !== 'LOCKED';
            return isOpen ? (
              <g key={`seg-${curr.id}`}>
                <line
                  x1={curr.posX}
                  y1={curr.posY}
                  x2={next.posX}
                  y2={next.posY}
                  stroke="var(--color-outline)"
                  strokeOpacity="0.65"
                  strokeWidth="8"
                  strokeLinecap="round"
                />
                <line
                  x1={curr.posX}
                  y1={curr.posY}
                  x2={next.posX}
                  y2={next.posY}
                  stroke="var(--color-primary)"
                  strokeWidth="4"
                  strokeLinecap="round"
                />
              </g>
            ) : (
              <line
                key={`seg-${curr.id}`}
                x1={curr.posX}
                y1={curr.posY}
                x2={next.posX}
                y2={next.posY}
                stroke="var(--color-paper)"
                strokeOpacity="0.82"
                strokeWidth="3.5"
                strokeDasharray="10 8"
                strokeLinecap="round"
              />
            );
          })}
        </svg>

        {/* Region Banner Pills */}
        {EXPLORATION_REGIONS.map((region) => {
          const unlocked = isRegionUnlocked(region.stageNumber);
          return (
            <button
              key={`region-${region.stageNumber}`}
              type="button"
              className={`map-region-pill ${!unlocked ? 'is-locked' : ''}`}
              style={{
                left: `${(region.bannerX / NATIVE_MAP_WIDTH) * 100}%`,
                top: `${(region.bannerY / NATIVE_MAP_HEIGHT) * 100}%`,
              }}
              data-testid={`region_banner_${region.name}`}
              aria-label={`Wilayah ${region.name}, status ${unlocked ? 'terbuka' : 'terkunci'}`}
              onClick={() => onRegionClick(region)}
            >
              <span aria-hidden="true">{region.iconEmoji}</span>
              <span>{region.shortName}</span>
              {!unlocked && <LockIcon size={12} />}
            </button>
          );
        })}

        {/* 50 Level Waypoint Nodes */}
        {levels.map((level) => {
          const isAvailable = level.status === 'AVAILABLE';
          const isCompleted = level.status === 'COMPLETED';
          const isMilestone = level.levelNumber === 10;
          const statusLabel = isCompleted
            ? `selesai dengan ${level.stars} bintang`
            : isAvailable
              ? 'tersedia untuk dimainkan'
              : 'masih terkunci';

          return (
            <button
              key={level.id}
              type="button"
              className="map-node-hitbox"
              style={{
                left: `${(level.posX / NATIVE_MAP_WIDTH) * 100}%`,
                top: `${(level.posY / NATIVE_MAP_HEIGHT) * 100}%`,
              }}
              data-testid={`level_node_${level.stageNumber}_${level.levelNumber}`}
              aria-label={`Level ${level.id}: ${level.title}, ${statusLabel}`}
              onClick={() => {
                if (level.status === 'LOCKED') {
                  setLockedAlert(`Selesaikan level sebelumnya untuk membuka ${level.id}!`);
                  return;
                }
                onLevelClick(level);
              }}
            >
              <span
                className={`map-node-circle ${
                  isCompleted
                    ? 'map-node-circle--completed'
                    : isAvailable
                      ? 'map-node-circle--available'
                      : 'map-node-circle--locked'
                }`}
              >
                {level.status === 'LOCKED' ? (
                  <LockIcon size={13} />
                ) : isMilestone && !isCompleted ? (
                  <FlagIcon size={14} />
                ) : (
                  level.id
                )}
              </span>

              {isCompleted && (
                <span className="map-node-stars" aria-hidden="true">
                  {[1, 2, 3].map((s) => (
                    <StarIcon
                      key={s}
                      size={9}
                      filled={s <= level.stars}
                      style={{
                        color: s <= level.stars ? 'var(--color-reward-strong)' : 'var(--color-locked)',
                      }}
                    />
                  ))}
                </span>
              )}
            </button>
          );
        })}

        {/* Animated Explorer Pin */}
        {activeLevel && (
          <div
            className="explorer-pin"
            style={{
              left: `${(activeLevel.posX / NATIVE_MAP_WIDTH) * 100}%`,
              top: `calc(${(activeLevel.posY / NATIVE_MAP_HEIGHT) * 100}% - 22px)`,
            }}
            data-testid="adventurer_explorer_pin"
            aria-hidden="true"
          >
            <div className="explorer-pin-badge">{companionEmoji}</div>
            <div className="explorer-pin-stem" />
          </div>
        )}
      </div>

      {/* Locked Toast Alert */}
      {lockedAlert && (
        <div
          role="status"
          aria-live="polite"
          style={{
            position: 'absolute',
            bottom: '70px',
            left: '50%',
            transform: 'translateX(-50%)',
            zIndex: 30,
            display: 'inline-flex',
            alignItems: 'center',
            gap: '8px',
            padding: '10px 16px',
            borderRadius: '12px',
            background: 'var(--color-bg)',
            color: 'var(--color-text-on-dark)',
            border: '1.5px solid var(--color-border-strong-on-dark)',
            fontSize: '0.84rem',
            fontWeight: 700,
            boxShadow: 'var(--shadow-elevated)',
          }}
        >
          <LockIcon size={16} />
          <span>{lockedAlert}</span>
        </div>
      )}

      {/* Bottom-Left Map Legend */}
      <div className="map-legend-anchor">
        {showLegend && (
          <div className="map-legend-card" data-testid="map_legend_card" role="region" aria-label="Legenda Peta">
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <strong style={{ fontFamily: 'var(--font-display)', fontSize: '0.8rem' }}>
                LEGENDA PETA
              </strong>
              <button
                type="button"
                aria-label="Tutup legenda"
                onClick={() => setShowLegend(false)}
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  width: '44px',
                  height: '44px',
                  border: 0,
                  borderRadius: '8px',
                  background: 'transparent',
                  cursor: 'pointer',
                  color: 'var(--color-text-on-paper)',
                }}
              >
                <CloseIcon size={16} />
              </button>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <span className="map-node-circle map-node-circle--completed" style={{ width: 20, height: 20, fontSize: 10 }}>✓</span>
              <span>Level Selesai &amp; Bintang</span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <span className="map-node-circle map-node-circle--available" style={{ width: 20, height: 20, fontSize: 10 }}>1</span>
              <span>Level Aktif Siap Dimainkan</span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <span className="map-node-circle map-node-circle--locked" style={{ width: 20, height: 20 }}><LockIcon size={10} /></span>
              <span style={{ color: 'var(--color-text-muted-on-paper)' }}>Jalur Belum Terbuka</span>
            </div>
            <button
              type="button"
              className="btn btn--secondary"
              style={{ minHeight: '44px', fontSize: '0.8rem', padding: '6px 10px' }}
              data-testid="legend_replay_guide_button"
              onClick={() => {
                setShowLegend(false);
                onReplayGuide();
              }}
            >
              <HelpIcon size={16} />
              <span>Putar Ulang Panduan</span>
            </button>
          </div>
        )}

        <button
          type="button"
          className="map-legend-btn"
          aria-expanded={showLegend}
          onClick={() => setShowLegend((prev) => !prev)}
        >
          <MapIcon size={16} />
          <span>Legenda</span>
        </button>
      </div>

      {/* Bottom-Right Map Control Dock */}
      <div className="map-zoom-dock" role="group" aria-label="Kontrol zoom dan fokus peta">
        <button
          type="button"
          className="map-dock-btn"
          aria-label="Pusatkan ke penjelajah"
          data-testid="recenter_explorer_button"
          onClick={() =>
            activeLevel && focusMapCoord(activeLevel.posX, activeLevel.posY, Math.max(scale, 1.35))
          }
        >
          <CenterTargetIcon size={18} />
        </button>
        <button
          type="button"
          className="map-dock-btn"
          aria-label="Reset posisi peta"
          data-testid="reset_map_button"
          onClick={() => {
            setScale(1.0);
            setOffset(clampPan(0, 0, 1.0));
          }}
        >
          <ResetIcon size={18} />
        </button>
        <button
          type="button"
          className="map-dock-btn"
          aria-label="Perbesar peta"
          data-testid="zoom_in_button"
          onClick={() => applyZoom(1.25)}
        >
          <ZoomInIcon size={18} />
        </button>
        <button
          type="button"
          className="map-dock-btn"
          aria-label="Perkecil peta"
          data-testid="zoom_out_button"
          onClick={() => applyZoom(0.8)}
        >
          <ZoomOutIcon size={18} />
        </button>
      </div>
    </div>
  );
}
