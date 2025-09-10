#!/bin/bash

# FastExcel Benchmark Runner Script
# Usage: ./benchmark-runner.sh [options]

set -euo pipefail

# Default configuration
DEFAULT_PROFILE="standard"
DEFAULT_OUTPUT_DIR="benchmark-results"
DEFAULT_JAVA_VERSION="11"
DEFAULT_HEAP_SIZE="4g"

# Script configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$(dirname "$SCRIPT_DIR")")"
TIMESTAMP="$(date +%Y%m%d_%H%M%S)"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

# Print usage information
print_usage() {
    cat << EOF
FastExcel Benchmark Runner

Usage: $0 [OPTIONS]

OPTIONS:
    -p, --profile PROFILE       Benchmark profile (quick|standard|comprehensive) [default: $DEFAULT_PROFILE]
    -o, --output DIR           Output directory for results [default: $DEFAULT_OUTPUT_DIR]
    -j, --java-version VERSION Java version to use [default: $DEFAULT_JAVA_VERSION]
    -m, --memory SIZE          JVM heap size [default: $DEFAULT_HEAP_SIZE]
    -t, --pattern PATTERN      Benchmark pattern to match
    -d, --dataset SIZE         Dataset size (SMALL|MEDIUM|LARGE|EXTRA_LARGE|ALL) [default: ALL]
    -f, --format FORMAT        Output format (json|csv|text) [default: json]
    -r, --regression           Enable regression analysis
    -v, --verbose              Enable verbose output
    -h, --help                 Show this help message

PROFILES:
    quick          Fast execution for development (2 warmup, 3 measurement, 1 fork)
    standard       Balanced execution for CI (3 warmup, 5 measurement, 1 fork)
    comprehensive  Thorough execution for nightly (5 warmup, 10 measurement, 2 forks)

EXAMPLES:
    # Run standard benchmarks
    $0 --profile standard

    # Run quick benchmarks for read operations only
    $0 --profile quick --pattern "ReadBenchmark"

    # Run comprehensive benchmarks with regression analysis
    $0 --profile comprehensive --regression

    # Run benchmarks for large datasets only
    $0 --dataset LARGE --memory 8g

    # Run with custom output directory
    $0 --output /tmp/benchmark-results --verbose
EOF
}

# Parse command line arguments
parse_arguments() {
    PROFILE="$DEFAULT_PROFILE"
    OUTPUT_DIR="$DEFAULT_OUTPUT_DIR"
    JAVA_VERSION="$DEFAULT_JAVA_VERSION"
    HEAP_SIZE="$DEFAULT_HEAP_SIZE"
    PATTERN=""
    DATASET="ALL"
    FORMAT="json"
    REGRESSION=false
    VERBOSE=false

    while [[ $# -gt 0 ]]; do
        case $1 in
            -p|--profile)
                PROFILE="$2"
                shift 2
                ;;
            -o|--output)
                OUTPUT_DIR="$2"
                shift 2
                ;;
            -j|--java-version)
                JAVA_VERSION="$2"
                shift 2
                ;;
            -m|--memory)
                HEAP_SIZE="$2"
                shift 2
                ;;
            -t|--pattern)
                PATTERN="$2"
                shift 2
                ;;
            -d|--dataset)
                DATASET="$2"
                shift 2
                ;;
            -f|--format)
                FORMAT="$2"
                shift 2
                ;;
            -r|--regression)
                REGRESSION=true
                shift
                ;;
            -v|--verbose)
                VERBOSE=true
                shift
                ;;
            -h|--help)
                print_usage
                exit 0
                ;;
            *)
                log_error "Unknown argument: $1"
                print_usage
                exit 1
                ;;
        esac
    done

    # Validate arguments
    if [[ ! "$PROFILE" =~ ^(quick|standard|comprehensive)$ ]]; then
        log_error "Invalid profile: $PROFILE"
        exit 1
    fi

    if [[ ! "$DATASET" =~ ^(SMALL|MEDIUM|LARGE|EXTRA_LARGE|ALL)$ ]]; then
        log_error "Invalid dataset size: $DATASET"
        exit 1
    fi

    if [[ ! "$FORMAT" =~ ^(json|csv|text)$ ]]; then
        log_error "Invalid output format: $FORMAT"
        exit 1
    fi
}

# Set up environment
setup_environment() {
    log_info "Setting up benchmark environment..."

    # Change to project directory
    cd "$PROJECT_DIR"

    # Create output directory
    mkdir -p "$OUTPUT_DIR"

    # Set JVM options
    export MAVEN_OPTS="-Xmx$HEAP_SIZE -XX:+UseG1GC"

    if [[ "$VERBOSE" == true ]]; then
        export MAVEN_OPTS="$MAVEN_OPTS -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"
    fi

    log_info "JVM Options: $MAVEN_OPTS"
    log_info "Output Directory: $OUTPUT_DIR"
}

# Configure benchmark parameters based on profile
configure_benchmark() {
    log_info "Configuring benchmark profile: $PROFILE"

    case $PROFILE in
        "quick")
            WARMUP_ITERATIONS=2
            MEASUREMENT_ITERATIONS=3
            FORKS=1
            ;;
        "standard")
            WARMUP_ITERATIONS=3
            MEASUREMENT_ITERATIONS=5
            FORKS=1
            ;;
        "comprehensive")
            WARMUP_ITERATIONS=5
            MEASUREMENT_ITERATIONS=10
            FORKS=2
            ;;
    esac

    # Build JMH arguments
    JMH_ARGS="-wi $WARMUP_ITERATIONS -i $MEASUREMENT_ITERATIONS -f $FORKS"

    # Output format
    case $FORMAT in
        "json")
            JMH_ARGS="$JMH_ARGS -rf json -rff ${OUTPUT_DIR}/benchmark-results-${TIMESTAMP}.json"
            ;;
        "csv")
            JMH_ARGS="$JMH_ARGS -rf csv -rff ${OUTPUT_DIR}/benchmark-results-${TIMESTAMP}.csv"
            ;;
        "text")
            JMH_ARGS="$JMH_ARGS -rf text -rff ${OUTPUT_DIR}/benchmark-results-${TIMESTAMP}.txt"
            ;;
    esac

    # Dataset size parameter
    if [[ "$DATASET" != "ALL" ]]; then
        JMH_ARGS="$JMH_ARGS -p datasetSize=$DATASET"
    fi

    # Benchmark pattern
    if [[ -n "$PATTERN" ]]; then
        JMH_ARGS="$JMH_ARGS $PATTERN"
    fi

    # Verbose output
    if [[ "$VERBOSE" == true ]]; then
        JMH_ARGS="$JMH_ARGS -v EXTRA"
    fi

    log_info "JMH Arguments: $JMH_ARGS"
}

# Build the project
build_project() {
    log_info "Building FastExcel project..."

    # Build and package the project
    if ! mvn clean package -U -DskipTests -q; then
        log_error "Failed to build FastExcel project"
        exit 1
    fi

    log_success "Project build completed"
}

# Validate benchmark setup
validate_setup() {
    log_info "Validating benchmark setup..."

    cd fastexcel-benchmark

    # Check if JMH can list benchmarks
    if ! mvn exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="-l" > benchmark-list.txt 2>/dev/null; then
        log_error "Failed to list available benchmarks"
        exit 1
    fi

    BENCHMARK_COUNT=$(wc -l < benchmark-list.txt)
    log_info "Found $BENCHMARK_COUNT available benchmarks"

    if [[ "$VERBOSE" == true ]]; then
        log_info "Available benchmarks:"
        cat benchmark-list.txt
    fi

    # Validate data generator
    if ! java -cp target/classes:target/test-classes \
         cn.idev.excel.benchmark.utils.DataGenerator --validate-only 2>/dev/null; then
        log_warn "Data generator validation failed, but proceeding..."
    fi

    rm -f benchmark-list.txt
    cd ..

    log_success "Benchmark setup validation completed"
}

# Run benchmarks
run_benchmarks() {
    log_info "Starting benchmark execution..."
    log_info "Profile: $PROFILE"
    log_info "Dataset: $DATASET"
    log_info "Expected duration: $(estimate_duration)"

    cd fastexcel-benchmark

    # Create results subdirectory with timestamp
    RESULTS_SUBDIR="${OUTPUT_DIR}/run-${TIMESTAMP}"
    mkdir -p "$RESULTS_SUBDIR"

    # Update JMH args to use subdirectory
    JMH_ARGS=$(echo "$JMH_ARGS" | sed "s|${OUTPUT_DIR}/|${RESULTS_SUBDIR}/|g")

    log_info "Running benchmarks..."

    if java -jar target/benchmarks.jar $JMH_ARGS; then
        log_success "Benchmark execution completed successfully"
    else
        local exit_code=$?
        log_error "Benchmark execution failed with exit code: $exit_code"
        exit $exit_code
    fi

    cd ..
}

# Estimate benchmark duration
estimate_duration() {
    local total_time=0

    case $PROFILE in
        "quick")
            total_time=$((($WARMUP_ITERATIONS + $MEASUREMENT_ITERATIONS) * $FORKS * 10))
            ;;
        "standard")
            total_time=$((($WARMUP_ITERATIONS + $MEASUREMENT_ITERATIONS) * $FORKS * 15))
            ;;
        "comprehensive")
            total_time=$((($WARMUP_ITERATIONS + $MEASUREMENT_ITERATIONS) * $FORKS * 20))
            ;;
    esac

    if [[ "$DATASET" == "ALL" ]]; then
        total_time=$((total_time * 4))  # Multiple dataset sizes
    fi

    if [[ -z "$PATTERN" ]]; then
        total_time=$((total_time * 10))  # Multiple benchmark classes
    fi

    echo "${total_time} seconds (~$((total_time / 60)) minutes)"
}

# Run regression analysis
run_regression_analysis() {
    if [[ "$REGRESSION" != true ]]; then
        return 0
    fi

    log_info "Running performance regression analysis..."

    cd fastexcel-benchmark

    # Find the latest results file
    local latest_results
    latest_results=$(find "${OUTPUT_DIR}" -name "*.json" -type f -printf '%T@ %p\n' | sort -n | tail -1 | cut -d' ' -f2-)

    if [[ -z "$latest_results" ]]; then
        log_warn "No benchmark results found for regression analysis"
        return 0
    fi

    # Run regression analysis
    if java -cp target/classes \
       cn.idev.excel.benchmark.regression.RegressionAnalysisRunner \
       "$latest_results" \
       --threshold 15.0 \
       --baseline-dir baseline-results \
       --output-dir "${OUTPUT_DIR}/analysis-${TIMESTAMP}"; then
        log_success "Regression analysis completed"
    else
        log_warn "Regression analysis detected performance issues"
        return 1
    fi

    cd ..
}

# Generate performance report
generate_report() {
    log_info "Generating performance report..."

    cd fastexcel-benchmark

    # Find the latest results file
    local latest_results
    latest_results=$(find "${OUTPUT_DIR}" -name "*.json" -type f -printf '%T@ %p\n' | sort -n | tail -1 | cut -d' ' -f2-)

    if [[ -z "$latest_results" ]]; then
        log_warn "No benchmark results found for report generation"
        return 0
    fi

    # Generate HTML report
    local report_file="${OUTPUT_DIR}/performance-report-${TIMESTAMP}.html"

    if java -cp target/classes \
       cn.idev.excel.benchmark.reporting.HtmlReportGenerator \
       "$latest_results" \
       --output "$report_file" \
       --include-charts \
       --java-version "$JAVA_VERSION"; then
        log_success "Performance report generated: $report_file"
    else
        log_warn "Failed to generate performance report"
    fi

    cd ..
}

# Print summary
print_summary() {
    log_info "Benchmark execution summary:"
    echo "  Profile: $PROFILE"
    echo "  Dataset: $DATASET"
    echo "  Output Directory: $OUTPUT_DIR"
    echo "  Timestamp: $TIMESTAMP"

    if [[ -d "${PROJECT_DIR}/fastexcel-benchmark/${OUTPUT_DIR}" ]]; then
        local result_count
        result_count=$(find "${PROJECT_DIR}/fastexcel-benchmark/${OUTPUT_DIR}" -name "*.json" -o -name "*.csv" -o -name "*.txt" | wc -l)
        echo "  Results Files: $result_count"

        if [[ "$FORMAT" == "json" ]]; then
            local latest_json
            latest_json=$(find "${PROJECT_DIR}/fastexcel-benchmark/${OUTPUT_DIR}" -name "*.json" -type f -printf '%T@ %p\n' | sort -n | tail -1 | cut -d' ' -f2-)
            if [[ -n "$latest_json" ]]; then
                echo "  Latest Results: $latest_json"
            fi
        fi
    fi

    log_success "Benchmark execution completed successfully!"
}

# Main execution
main() {
    log_info "FastExcel Benchmark Runner Starting..."

    parse_arguments "$@"
    setup_environment
    configure_benchmark
    build_project
    validate_setup
    run_benchmarks

    local regression_exit_code=0
    if ! run_regression_analysis; then
        regression_exit_code=1
    fi

    generate_report
    print_summary

    if [[ $regression_exit_code -ne 0 ]]; then
        log_warn "Benchmark completed with performance regression warnings"
        exit 1
    fi

    log_success "All benchmark operations completed successfully"
}

# Handle script interruption
trap 'log_error "Script interrupted"; exit 130' INT TERM

# Execute main function
main "$@"
